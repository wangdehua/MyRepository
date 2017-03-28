package com.wdh.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wdh.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userServiceImpl ;
	/**
	 * 导入user数据
	 * @return
	 */
	@RequestMapping(value="/user/import",method=RequestMethod.POST)
	@ResponseBody
	public Map importUser(HttpServletRequest request,@RequestParam(value="file")MultipartFile file){
		return this.userServiceImpl.importUser(request,file);
	}
	
	/**
	 * 导出user
	 * @return
	 */
	@RequestMapping(value="/user/export")
	@ResponseBody
	public Map exportUser(HttpServletResponse response){
		return this.userServiceImpl.exportUser(response);
	}
}
