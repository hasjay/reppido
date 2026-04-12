package com.hasjay.reppido.exception;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(Integer id) {
        super("Report not found with id: " + id);
    }
}
