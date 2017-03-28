package com.wdh.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	Map importUser(HttpServletRequest request, MultipartFile file);

	Map exportUser(HttpServletResponse response);

}
