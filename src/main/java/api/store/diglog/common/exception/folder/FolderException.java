package api.store.diglog.common.exception.folder;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FolderException extends RuntimeException {

	private final HttpStatus status;

	public FolderException(HttpStatus status, String errorMessage) {
		super(errorMessage);
		this.status = status;
	}

}
