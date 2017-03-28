package com.wdh.service.impl;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wdh.mapper.UserMapper;
import com.wdh.pojo.User;
import com.wdh.service.UserService;
import com.wdh.util.ImportUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	/**
	 * 导入
	 */
	public Map importUser(HttpServletRequest request, MultipartFile file) {
		// 创建返回的集合
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (!((MultipartFile) file).isEmpty()) {
				// 文件不为空保存到webapp下
				String realPath = request.getSession().getServletContext().getRealPath("\\upload");
				System.out.println(realPath);
				// 修改文件名称
				int index = (file).getOriginalFilename().indexOf('.');
				String prefix = (file).getOriginalFilename().substring(index);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				// 给上传的文件一个新的名称
				String fileName = sdf.format(new Date()) + prefix;
				// 设置存放文件的路径
				String path = realPath + "\\" + fileName;
				System.out.println("存放文件的路径:" + path);
				// 转存文件到指定的路径
				file.transferTo(new File(path));
				// 读取excle
				int i = 0;
				File file2 = new File(path);
				// 调用工具类
				List<List<Object>> list = ImportUtil.readExcel(file2);
				// 遍历
				for (List<Object> list2 : list) {
					// 表头不读取
					if (i++ == 0)
						continue;
					// 读取到空结束
					if (list2 == null || list2.size() == 0)
						break;
					User user = new User();
					user.setId(Integer.parseInt(list2.get(0).toString()));
					user.setName(list2.get(1).toString());
					user.setSex(list2.get(2).toString());
					user.setDid(Integer.parseInt(list2.get(3).toString()));
					// 对导入的数据进行判断
					// 如果对相应的id已存在,进行修改否则添加
					// 根据id查询user(这里用user.getId())
					User exuser = this.userMapper.selectByPrimaryKey(user.getId());
					if (exuser != null && !exuser.equals("")) {
						// 存在user进行修改
						this.userMapper.updateByPrimaryKey(exuser);
					} else {
						// 添加
						this.userMapper.insert(user);
					}
				}
				resultMap.put("flag", 1);
				resultMap.put("status", 1);
				resultMap.put("msg", "OK!");
			} else {
				resultMap.put("flag", 1);
				resultMap.put("status", 0);
				resultMap.put("msg", "文件为空!");
			}
		} catch (Exception e) {
			resultMap.put("flag", 1);
			resultMap.put("status", 0);
			resultMap.put("msg", "NO!");
		}
		return resultMap;
	}

	/**
	 * 导出
	 */
	public Map exportUser(HttpServletResponse response) {
		// 创建返回信息
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 取到的数据
		List<User> list = this.userMapper.selectAll();
		// 定义一个导出的header,数据库表头信息
		String[] excelHeader = { "id", "姓名", "性别", "部门编号", "部门名称" };
		// 创建导出的魔板
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Campaign");
		HSSFCellStyle style = wb.createCellStyle();
		HSSFRow rows = sheet.createRow((int) 0);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		for (int i = 0; i < excelHeader.length; i++) {
			HSSFCell cell = rows.createCell(i);
			cell.setCellValue(excelHeader[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
		}
		// 遍历取出的数据
		for (int i = 0; i < list.size(); i++) {
			// 创建第i行
			User user = list.get(i);
			// 因为第一行是表头,已经存在了
			rows = sheet.createRow(i + 1);
			// 创建单元格,为每个单元格设置数据的存放类型
			rows.createCell(0).setCellValue(user.getId());
			rows.createCell(1).setCellValue(user.getName());
			rows.createCell(2).setCellValue(user.getSex());
			rows.createCell(3).setCellValue(user.getDid());
			rows.createCell(4).setCellValue(user.getDname());
		}
		try {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=user.xls");
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
			resultMap.put("flag", 1);
			resultMap.put("status", 1);
			resultMap.put("msg", "OK");
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("flag", 1);
			resultMap.put("status", 0);
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
	}
}
