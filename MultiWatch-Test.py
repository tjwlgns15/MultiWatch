"""
MultiWatch 디바이스 신호 시뮬레이터.

흐름:
  1) POST /api/organizations 로 조직 + 관리자 계정 회원가입 (apiKey를 SignUpResponse에서 바로 받음)
  2) 방금 만든 관리자 계정으로 로그인 (세션 쿠키 획득)
  3) 디바이스 N개를 세션으로 등록 (POST /api/devices)
  4) 각 디바이스에 대해 주기적으로 X-API-KEY 헤더를 붙여 POST /api/gateway/readings 전송

이미 가입해둔 조직으로 계속 테스트하고 싶다면 EXISTING_ADMIN_EMAIL/PASSWORD를 채우고
SIGN_UP을 False로 바꾸세요.
"""

import random
import time
from dataclasses import dataclass, field
from datetime import datetime

import requests

BASE_URL = "http://localhost:8027"

SIGN_UP = True  # False면 아래 EXISTING_* 계정으로 로그인만 하고 회원가입은 건너뜀

# SIGN_UP=True일 때 사용할 신규 가입 정보
ORGANIZATION_NAME = "시뮬레이터 테스트 조직"
NEW_ADMIN_EMAIL = f"sim-{random.randint(1000, 9999)}@example.com"
NEW_ADMIN_PASSWORD = "password123!"

# SIGN_UP=False일 때 사용할 기존 계정 정보 (apiKey는 최초 회원가입 시 받은 값을 그대로 채워주세요)
EXISTING_ADMIN_EMAIL = "admin@example.com"
EXISTING_ADMIN_PASSWORD = "password123!"
EXISTING_API_KEY = "REPLACE_ME"

DEVICE_COUNT = 5
SEND_INTERVAL_SECONDS = 3

# 알림 규칙 테스트를 위해 가끔 임계값을 넘는 스파이크 값을 섞어 보낼 확률
SPIKE_PROBABILITY = 0.1


@dataclass
class DeviceProfile:
    """디바이스 종류별로 그럴듯한 값 범위를 다르게 준다."""
    device_type: str
    name: str
    normal_range: tuple
    spike_range: tuple
    device_key: str = field(default=None)

    def next_value(self) -> float:
        if random.random() < SPIKE_PROBABILITY:
            return round(random.uniform(*self.spike_range), 2)
        return round(random.uniform(*self.normal_range), 2)


def build_profiles(count: int) -> list[DeviceProfile]:
    templates = [
        DeviceProfile("TEMPERATURE", "온도센서", (18.0, 32.0), (85.0, 110.0)),
        DeviceProfile("VIBRATION", "진동센서", (0.5, 3.5), (8.0, 15.0)),
        DeviceProfile("POWER", "전력센서", (200.0, 1500.0), (4000.0, 6000.0)),
    ]
    return [
        DeviceProfile(t.device_type, f"{t.name}-{i+1}", t.normal_range, t.spike_range)
        for i, t in enumerate(templates * ((count // len(templates)) + 1))
    ][:count]


def sign_up() -> tuple[str, str, str]:
    """조직 + 관리자 계정을 새로 만들고 (email, password, apiKey)를 반환한다."""
    res = requests.post(
        f"{BASE_URL}/api/organizations",
        json={
            "organizationName": ORGANIZATION_NAME,
            "adminEmail": NEW_ADMIN_EMAIL,
            "adminPassword": NEW_ADMIN_PASSWORD,
        },
    )
    res.raise_for_status()
    body = res.json()
    print(f"[회원가입 성공] organizationId={body['organizationId']} email={NEW_ADMIN_EMAIL}")
    return NEW_ADMIN_EMAIL, NEW_ADMIN_PASSWORD, body["apiKey"]


def login(session: requests.Session, email: str, password: str):
    res = session.post(
        f"{BASE_URL}/api/auth/login",
        json={"email": email, "password": password},
    )
    res.raise_for_status()
    print(f"[로그인 성공] {email}")


def register_devices(session: requests.Session, profiles: list[DeviceProfile]):
    for profile in profiles:
        device_key = f"sim-{profile.device_type.lower()}-{random.randint(1000, 9999)}"
        res = session.post(
            f"{BASE_URL}/api/devices",
            json={
                "deviceKey": device_key,
                "name": profile.name,
                "deviceType": profile.device_type,
            },
        )
        res.raise_for_status()
        profile.device_key = device_key
        print(f"[디바이스 등록] {profile.name} ({device_key}) -> deviceId={res.json()}")


def send_readings(profiles: list[DeviceProfile], api_key: str):
    headers = {"X-API-KEY": api_key, "Content-Type": "application/json"}

    while True:
        payload = [
            {
                "deviceKey": profile.device_key,
                "value": profile.next_value(),
                "recordedAt": datetime.now().isoformat(timespec="seconds"),
            }
            for profile in profiles
        ]

        res = requests.post(f"{BASE_URL}/api/gateway/readings", json=payload, headers=headers)
        status = res.status_code
        summary = ", ".join(f"{p['deviceKey']}={p['value']}" for p in payload)
        print(f"[{datetime.now():%H:%M:%S}] status={status} {summary}")

        time.sleep(SEND_INTERVAL_SECONDS)


def main():
    if SIGN_UP:
        email, password, api_key = sign_up()
    else:
        if EXISTING_API_KEY == "REPLACE_ME":
            raise SystemExit(
                "SIGN_UP=False로 기존 계정을 쓰려면 EXISTING_API_KEY를 채워주세요. "
                "회원가입 시 SignUpResponse로 받았던 apiKey를 그대로 넣으면 됩니다."
            )
        email, password, api_key = EXISTING_ADMIN_EMAIL, EXISTING_ADMIN_PASSWORD, EXISTING_API_KEY

    session = requests.Session()
    login(session, email, password)

    profiles = build_profiles(DEVICE_COUNT)
    register_devices(session, profiles)

    print(f"\n{SEND_INTERVAL_SECONDS}초 간격으로 신호 전송 시작 (Ctrl+C로 중단)\n")
    try:
        send_readings(profiles, api_key)
    except KeyboardInterrupt:
        print("\n중단됨.")


if __name__ == "__main__":
    main()