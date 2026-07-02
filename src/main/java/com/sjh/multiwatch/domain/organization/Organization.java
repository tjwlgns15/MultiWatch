package com.sjh.multiwatch.domain.organization;

import com.sjh.multiwatch.domain.ApiKeyGenerator;
import com.sjh.multiwatch.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizations")
@Getter
@NoArgsConstructor
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String apiKey;

    private Organization(String name, String apiKey) {
        this.name = name;
        this.apiKey = apiKey;
    }

    public static Organization register(String name) {
        return new Organization(name, ApiKeyGenerator.generate());
    }
}
