package api.store.diglog.common.exception;

import java.awt.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import api.store.diglog.common.exception.folder.FolderException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = new ErrorResponse(errorCode);

		return new ResponseEntity<>(errorResponse, errorCode.getStatus());
	}

	// Validation 에러
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = new ErrorResponse(e);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FolderException.class)
	public ResponseEntity<ErrorResponse> handleFolderException(FolderException folderException) {
		ErrorResponse errorResponse = new ErrorResponse(
			String.valueOf(folderException.getStatus().value()),
			folderException.getMessage()
		);

		return ResponseEntity.status(folderException.getStatus()).body(errorResponse);
	}
}
