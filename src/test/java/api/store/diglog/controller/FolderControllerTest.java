package api.store.diglog.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.store.diglog.model.constant.Platform;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.dto.folder.FolderCreateRequest;
import api.store.diglog.model.dto.folder.FolderResponse;
import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;
import api.store.diglog.service.FolderService;

@WebMvcTest(controllers = FolderController.class)
class FolderControllerTest {

	private static final String EMAIL = "diglog@example.com";
	private static final String PASSWORD = "qwer1234";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private FolderService folderService;

	@DisplayName("폴더 목록을 생성할 수 있다.")
	@Test
	@WithMockUser(username = EMAIL, password = PASSWORD)
	void create() throws Exception {

		List<FolderCreateRequest> folderCreateRequests = List.of(
			FolderCreateRequest.builder()
				.title("title01")
				.depth(0)
				.orderIndex(0)
				.build(),
			FolderCreateRequest.builder()
				.title("title02")
				.depth(0)
				.orderIndex(1)
				.build(),
			FolderCreateRequest.builder()
				.title("title03")
				.depth(0)
				.orderIndex(2)
				.build()
		);

		Member member = Member.builder()
			.email(EMAIL)
			.username("testUser")
			.password(PASSWORD)
			.roles(Set.of(Role.ROLE_USER))
			.platform(Platform.SERVER)
			.createdAt(LocalDateTime.of(2022, 2, 22, 12, 0))
			.updatedAt(LocalDateTime.of(2022, 3, 22, 12, 0))
			.build();

		List<Folder> folders = List.of(
			Folder.builder()
				.id(UUID.randomUUID())
				.member(member)
				.title("title01")
				.depth(0)
				.orderIndex(0)
				.build(),
			Folder.builder()
				.id(UUID.randomUUID())
				.member(member)
				.title("title02")
				.depth(0)
				.orderIndex(1)
				.build(),
			Folder.builder()
				.id(UUID.randomUUID())
				.member(member)
				.title("title03")
				.depth(0)
				.orderIndex(2)
				.build()
		);

		List<FolderResponse> folderResponses = folders.stream()
			.map(folder -> FolderResponse.builder()
				.folder(folder)
				.build())
			.toList();

		BDDMockito.given(folderService.createAndUpdateFolders(anyList()))
			.willReturn(folderResponses);

		mockMvc.perform(
				post("/api/folders")
					.with(csrf())
					.content(objectMapper.writeValueAsString(folderCreateRequests))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].folderId").value(folders.get(0).getId().toString()))
			.andExpect(jsonPath("$[0].title").value("title01"))
			.andExpect(jsonPath("$[0].depth").value(0))
			.andExpect(jsonPath("$[0].orderIndex").value(0))
			.andExpect(jsonPath("$[0].parentFolderId").value("none"))
			.andExpect(jsonPath("$[1].folderId").value(folders.get(1).getId().toString()))
			.andExpect(jsonPath("$[1].title").value("title02"))
			.andExpect(jsonPath("$[1].depth").value(0))
			.andExpect(jsonPath("$[1].orderIndex").value(1))
			.andExpect(jsonPath("$[1].parentFolderId").value("none"))
			.andExpect(jsonPath("$[2].folderId").value(folders.get(2).getId().toString()))
			.andExpect(jsonPath("$[2].title").value("title03"))
			.andExpect(jsonPath("$[2].depth").value(0))
			.andExpect(jsonPath("$[2].orderIndex").value(2))
			.andExpect(jsonPath("$[2].parentFolderId").value("none"));
	}
}