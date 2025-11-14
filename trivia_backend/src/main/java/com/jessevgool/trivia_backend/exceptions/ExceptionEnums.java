package com.jessevgool.trivia_backend.exceptions;

import lombok.Getter;

/**
 *
 * @author Jesse van Gool
 */
@Getter
public enum ExceptionEnums {
    OPEN_TDB_RATE_LIMIT(429),
    OPEN_TDB_TOKEN_EXCEPTION(400),
    OPEN_TDB_EXCEPTION(502);

    private final int code;

    ExceptionEnums(int code) {
        this.code = code;
    }

    public int getCode () {
        return code;
    }
}