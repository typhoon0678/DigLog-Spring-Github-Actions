package api.store.diglog.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.store.diglog.model.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

    Optional<Folder> findByIdAndMemberId(UUID id, UUID memberId);

    List<Folder> findAllByIdIn(List<UUID> folderIds);
}
