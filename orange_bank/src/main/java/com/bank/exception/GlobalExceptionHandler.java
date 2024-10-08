package com.bank.exception;

import com.bank.dto.CommonApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FailedToRegisterBankException.class)
    public ResponseEntity<CommonApiResponse> handleUserNotFoundException(FailedToRegisterBankException failedToRegisterBankException) {
        CommonApiResponse response = CommonApiResponse.builder()
                .responseMessage(failedToRegisterBankException.getMessage())
                .isSuccess(true)
                .build();
        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<CommonApiResponse> handleTransactionException(TransactionException exception) {
        CommonApiResponse response = CommonApiResponse.builder().responseMessage(exception.getMessage()).isSuccess(true).build();
        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
