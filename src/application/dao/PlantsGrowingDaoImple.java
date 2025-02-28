package application.dao;

import java.sql.*;

// 패키지명 수정 (com -> application)
import application.dao.*;

import static common.JdbcTemplate.*;
import application.firstLogin.Users.UserInfo;
import static common.JdbcTemplate.*;

public class PlantsGrowingDaoImple implements PlantsGrowingDao {
	
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement psmt = null;
	
	// ID 로그인 시 ID Password 가 맞는지 확인
	// sql문 작성하여 Statement로 보내주고, ResultSet으로 결과 받아줌
	public boolean check(String ID, String password) throws SQLException {
		try {
			conn = getConnection();
			psmt = conn.prepareStatement(members_selectAll);
			psmt.setString(1, ID);
			psmt.setString(2, password);
			rs = psmt.executeQuery();
			if(rs.next()) {
				System.out.println("[Id / pw match]데이터 베이스 check 완료 : " + ID +" " + password);
				return true;
			}
					
		} catch (Exception e) {
			System.out.println("데이터 베이스 check 검색 오류 : "  + e.getMessage());
		}finally {
			close(rs);
			close(psmt);
			close(conn);
		}
		return false;
	}
	
	// ID 중복확인
	// sql문 작성하여 Statement로 보내주고, ResultSet으로 결과 받아줌
	public boolean checkDuplicate(String ID) throws SQLException {
		try {
			conn = getConnection();
			psmt = conn.prepareStatement(members_selectId);
			psmt.setString(1, ID);
			rs = psmt.executeQuery();
			if(rs.next()) {
				System.out.println("[중복된 id] 데이터 베이스 check 완료: " + ID);
				return true;
			}
					
		} catch (Exception e) {
			System.out.println("데이터 베이스 checkDuplicate 검색 오류 : "  + e.getMessage());
			
		}finally {
			close(rs);
			close(psmt);
			close(conn);
		}
		return false;
	}
	
	// 아이디 생성시 DB업데이트
	// userinfo id, pw만 있는 생성자 버전 사용해서 업뎃하면 됨.
	public void createId(UserInfo userInfo) throws SQLException {
		
		try {
			conn = getConnection();
			psmt = conn.prepareStatement(members_insert);
			psmt.setString(1, userInfo.getId());
			psmt.setString(2, userInfo.getPassword());
			psmt.setString(3, userInfo.getIP());
			psmt.setInt(4, userInfo.getPort());
			psmt.executeUpdate();
			
			System.out.println("[계정생성 완료] id = " + userInfo.getId() +" pw = " + userInfo.getPassword());
			//커밋
			commit(conn);
		} catch(SQLException se) {
			System.out.println("계정 생성 오류");
			se.printStackTrace();
			//롤 백 
			rollBack(conn);
		} finally {
			close(psmt);
			close(conn);
		}
	}
	
	// 식물 종류 선택
	public void updatePlantSpecies(UserInfo userInfo) throws SQLException {
		try {

			conn = getConnection();
			psmt = conn.prepareStatement(members_updatePlantSpecies);
			psmt.setInt(1, userInfo.getSpecies());
			psmt.setString(2, userInfo.getId());
			psmt.executeUpdate();
			
			System.out.printf("데이터 업데이트 완료[id = %1s / plantName = %1s] \n",
								userInfo.getId(), userInfo.getSpecies() );
			commit(conn);
		} catch(SQLException se) {
			System.out.println("식물 종 업데이트 오류");
			se.printStackTrace();
			rollBack(conn);
		} finally {
			close(psmt);
			close(conn);
		}
	}
	
	
	// 닉네임 세팅 때 아이디로 세팅
	public void updatePlantName(UserInfo userInfo) throws SQLException {
		try {
			
			conn = getConnection();
			psmt = conn.prepareStatement(members_updatePlantName);
			psmt.setString(1, userInfo.getPlantName());
			psmt.setString(2, userInfo.getId());
			psmt.executeUpdate();
			
			System.out.printf("데이터 업데이트 완료[id = %1s / plantName = %1s] \n",
								userInfo.getId(), userInfo.getPlantName() );
			commit(conn);
		} catch(SQLException se) {
			System.out.println("식물 이름 업데이트 오류");
			se.printStackTrace();
			rollBack(conn);
		}finally {
			close(psmt);
			close(conn);
		}
	}

	// 메인페이지에서 아이디가 드러나지 않을때 닉네임으로 세팅하는기능
	public void updateAll(UserInfo userInfo) throws SQLException {
		try {
			
			conn = getConnection();
			psmt = conn.prepareStatement(members_updateAll);
			psmt.setInt(1, userInfo.getWatering());
			psmt.setInt(2, userInfo.getCaring());
			psmt.setInt(3, userInfo.getTanning());
			psmt.setInt(4, userInfo.getNutrition());
			psmt.setInt(5, userInfo.getLevel());
			psmt.setString(6, userInfo.getPlantName());
			psmt.executeUpdate();
			
			System.out.printf("데이터 업데이트 완료[id = %1s] 물 = %1d 사랑 = %1d 햇빛 = %1d 양분 = %1d \n",
								userInfo.getId(), userInfo.getWatering(), userInfo.getCaring(), userInfo.getTanning(), userInfo.getNutrition() );
			commit(conn);
		} catch(SQLException se) {
			System.out.println("데이터 업데이트 오류");
			se.printStackTrace();
			rollBack(conn);
		} finally {
			close(psmt);
			close(conn);
		}
	}
	
	//데이터 업로드, db의 데이터를 게임 내 정보로 가져오기
	public void loadInfo(UserInfo userInfo,String id) throws SQLException {
		try {
			
			conn = getConnection();
			psmt = conn.prepareStatement(members_selectId);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			
			if(rs.next()) {
				userInfo.setId(rs.getString(1));
				userInfo.setPassword(rs.getString(2));
				userInfo.setPlantName(rs.getString(3));
				userInfo.setWatering(rs.getInt(4));
				userInfo.setCaring(rs.getInt(5));
				userInfo.setTanning(rs.getInt(6));
				userInfo.setNutrition(rs.getInt(7));
				userInfo.setIP(rs.getString(8));
				userInfo.setPort(rs.getInt(9));
				userInfo.setSpecies(rs.getInt(10));
				userInfo.setLevel(rs.getInt(11));
				System.out.printf("[userInfo 불러오기 완료] id = %s / PlantName = %s / Watering = %d / Caring = %d / Tanning = %d / Nutrition = %d / Level = %d / Species = %d\n" ,
						userInfo.getId() , userInfo.getPlantName(), userInfo.getWatering(), userInfo.getCaring(), 
						userInfo.getTanning(), userInfo.getNutrition(), userInfo.getLevel(),userInfo.getSpecies());
			}
			
			
		}catch (Exception e) {
			System.out.println("데이터 불러오기 오류");
			e.printStackTrace();
		}finally {
			close(rs);
			close(psmt);
			close(conn);
		}
	}
	
	// 행운의 포춘쿠키 뽑기
	public String pickFortune() throws SQLException {
		try {
			
			conn = getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(fortune_pick);
			if(rs.next()) {
				String ft =rs.getString(1);
				System.out.println("[포춘 쿠키]운세 가져오기 성공 : " + ft);
				
				return ft;
			}
			
		}catch(Exception e) {
			System.out.println("운세가져오기 오류");
		}finally {
			close(rs);
			close(st);
			close(conn);
		}
		return "잘 지내봐요";
	}
	
	
	
}
//Connection conn = null;
//Statement st = null;
//ResultSet rs = null;
//PreparedStatement psmt = null;