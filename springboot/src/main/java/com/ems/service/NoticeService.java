package com.ems.service;

import com.ems.entity.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> list();
    List<Notice> listByTargetRole(String role);
    Notice getById(Long id);
    Notice create(Notice notice, Long publisherId);
    void update(Notice notice);
    void delete(Long id);
}
