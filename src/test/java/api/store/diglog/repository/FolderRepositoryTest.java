package api.store.diglog.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import api.store.diglog.model.constant.Platform;
import api.store.diglog.model.constant.Role;
import api.store.diglog.model.entity.Folder;
import api.store.diglog.model.entity.Member;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FolderRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	FolderRepository folderRepository;

	@DisplayName("회원 이름으로 폴더를 조회할 수 있다")
	@Test
	void findAllByMember() {

		// given
		Member member = Member.builder()
			.email("testEmail@gmail.com")
			.username("testUser")
			.password("testPassword")
			.roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
			.platform(Platform.SERVER)
			.createdAt(LocalDateTime.of(2022, 2, 22, 12, 0))
			.updatedAt(LocalDateTime.of(2022, 3, 22, 12, 0))
			.build();

		memberRepository.save(member);

		Folder test01 = Folder.builder()
			.id(UUID.randomUUID())
			.member(member)
			.title("test01")
			.depth(0)
			.orderIndex(0)
			.parentFolder(null)
			.build();
		Folder test02 = Folder.builder()
			.id(UUID.randomUUID())
			.member(member)
			.title("test02")
			.depth(1)
			.orderIndex(1)
			.parentFolder(test01)
			.build();
		Folder test03 = Folder.builder()
			.id(UUID.randomUUID())
			.member(member)
			.title("test03")
			.depth(2)
			.orderIndex(2)
			.parentFolder(test02)
			.build();

		folderRepository.saveAll(List.of(test01, test02, test03));

		// when
		List<Folder> folders = folderRepository.findAllByMemberWithParent(member);

		// then
		assertThat(folders).hasSize(3)
			.contains(test01, test02, test03);

	}
}