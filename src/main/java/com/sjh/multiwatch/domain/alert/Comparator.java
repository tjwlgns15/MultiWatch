package com.sjh.multiwatch.domain.alert;

public enum Comparator {
    GT {
        @Override
        public boolean isViolated(Double value, Double threshold) {
            return value > threshold;
        }
    },
    LT {
        @Override
        public boolean isViolated(Double value, Double threshold) {
            return value < threshold;
        }
    };

    public abstract boolean isViolated(Double value, Double threshold);
}
