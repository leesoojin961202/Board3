package com.koreait.board3.board.cmt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.koreait.board3.common.SecurityUtils;
import com.koreait.board3.common.Utils;

@WebServlet("/board/cmt/reg")
public class BoardCmtRegSer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(SecurityUtils.isLogout(request)) {  
			response.sendRedirect("/login"); 
			return;
		}
		
		response.sendRedirect(BoardCmtService.regCmt(request));
	}
}
