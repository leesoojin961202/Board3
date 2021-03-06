package com.koreait.board3.board;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.koreait.board3.common.SecurityUtils;
import com.koreait.board3.common.Utils;
import com.koreait.board3.db.BoardCmtDAO;
import com.koreait.board3.db.BoardDAO;
import com.koreait.board3.db.SQLInterUpdate;
import com.koreait.board3.model.BoardCmtSEL;
import com.koreait.board3.model.BoardParam;
import com.koreait.board3.model.BoardSEL;

public class BoardService {
	
//	글 목록 확인
	public static List<BoardSEL> showListAll(HttpServletRequest request){
		int typ = Utils.getIntParam(request, "typ");
		BoardParam param = new BoardParam();
		param.setTyp(typ);
		
		return BoardDAO.showListAll(param);
	}
	
// 	글 읽기
	public static BoardSEL readCtnt(HttpServletRequest request) {
		int i_board = Utils.getIntParam(request, "i_board");
		if(i_board == 0) {
			return null;
		}
		
		BoardParam param = new BoardParam();
		param.setI_board(i_board);
		param.setI_user(SecurityUtils.getLoginI_User(request));
		
		request.setAttribute("cmtCtnt", BoardCmtDAO.showListCmt(param));
		
		return BoardDAO.readCtnt(param);
	}
	
//	글 등록 / 글 수정
	public static String regMod(HttpServletRequest request) {
		int i_board = Utils.getIntParam(request, "i_board");
		int typ = Utils.getIntParam(request, "typ");
		String title = request.getParameter("title");
		String ctnt = request.getParameter("ctnt");
		int i_user = SecurityUtils.getLoginI_User(request);
		
		if(i_board == 0) { // 등록
			String sql = " INSERT INTO t_board "
						 + " (typ, seq, title, ctnt, i_user) "
						 + " SELECT "
//						 	    seq가 NULL이면 0으로 하고 +1(seq - null : 글이 하나도 없을때)
						 + " ?, IFNULL(MAX(seq), 0) + 1, ?, ?, ? " 
						 + " FROM t_board "
						 + " WHERE typ = ? ";
			
			BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
				
				@Override
				public void proc(PreparedStatement pstmt) throws SQLException {
					pstmt.setInt(1, typ);
					pstmt.setNString(2, title);
					pstmt.setNString(3, ctnt);
					pstmt.setInt(4, i_user);
					pstmt.setInt(5, typ);
				}
			});		
		 return "bDetail?i_board=" + i_board;
		}else { // 수정
			String sql = " UPDATE t_board "
						 + " SET title = ?, ctnt = ? "
						 + " WHERE i_board = ? "
						 + " AND i_user = ? ";
			
			BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
				
				@Override
				public void proc(PreparedStatement pstmt) throws SQLException {
					pstmt.setNString(1, title);
					pstmt.setNString(2, ctnt);
					pstmt.setInt(3, i_board);
					pstmt.setInt(4, SecurityUtils.getLoginI_User(request));
				} // 업데이트되면 1이 넘어온다
			});
		 return "list?typ=" + typ;
		}
	}
	
//	글 삭제
	public static int delBoard(HttpServletRequest request) {
		int i_board = Utils.getIntParam(request, "i_board");
		int i_user = SecurityUtils.getLoginI_User(request);
		
		String sql = " DELETE FROM t_board "
				 	 + " WHERE i_board = ? "
				 	 + " AND i_user = ? ";
		
		return BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
			
			@Override
			public void proc(PreparedStatement pstmt) throws SQLException {
				pstmt.setInt(1, i_board);
				pstmt.setInt(2, i_user);
			}
		});
	}
	
//	좋아요 기능 처리 2
	public static String ajax_favorite(HttpServletRequest request) {
		int result = 0;
		int state = Utils.getIntParam(request, "state");
		int i_board = Utils.getIntParam(request, "i_board");
		int i_user = SecurityUtils.getLoginI_User(request);
		
		String sql = null;
		
		switch (state) {
		case 0:  // 좋아요 해제 
			sql = " DELETE FROM t_board_favorite "
				  + " WHERE i_board = ? "
				  + " AND i_user = ? ";
			break;
		case 1:  // 좋아요 클릭
			sql = " INSERT INTO t_board_favorite "
				  + " (i_board, i_user) "
				  + " VALUES "
				  + " (?, ?) ";
			break;
		}
		
		result = BoardDAO.executeUpdate(sql, new SQLInterUpdate() {
			
			@Override
			public void proc(PreparedStatement pstmt) throws SQLException {
				pstmt.setInt(1, i_board);
				pstmt.setInt(2, i_user);
			}
		});
		
//		문제 발생 시 0이 넘어오고 처리완료 시 1이 넘어온다 
		return String.format("{ \"result\" : %d}", result);
	}
}
