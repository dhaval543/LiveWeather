package com.anytime.weather.exception;

public enum NotifyType {
    SMS("text sms"),
    EMAIL("email"),
    SLACK("slack");

    String value;

    NotifyType(String value) {
        this.value = value;
    }

    String getValue() {
        return this.value;
    }
}
