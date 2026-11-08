package io.github.xaxis.rmvconflicts.activity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileActivityRepository
    extends JpaRepository<FileActivity, Long>,
        JpaSpecificationExecutor<FileActivity> {

  List<FileActivity> findByProject_Id(Long projectId);

  List<FileActivity> findByUser_Id(Long userId);

  FileActivity getFileActivityObservationByProject_IdAndUser_Id(
      Long projectId, Long userId);

  void deleteByUser_Id(Long userId);

  List<FileActivity> getFileActivityObservationsByUser_Id(Long userId);

  List<FileActivity> getFileActivityObservationsByUser_IdAndProject_Id(
      Long userId, Long projectId);
}
