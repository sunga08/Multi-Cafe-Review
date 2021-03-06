package kr.co.multicafe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.multicafe.dao.LikesMapper;
import kr.co.multicafe.dao.MenuMapper;
import kr.co.multicafe.dao.RecentMapper;
import kr.co.multicafe.dao.ReviewMapper;
import kr.co.multicafe.dto.Likes;
import kr.co.multicafe.dto.Menu;
import kr.co.multicafe.dto.Recent;
import kr.co.multicafe.dto.Taste;

@Service
@Transactional
public class MenuService {
	
	@Autowired
	private MenuMapper menuMapper;
	
	@Autowired
	private LikesMapper likesMapper;
	
	@Autowired
	private RecentMapper recentMapper;

	@Autowired
	private ReviewMapper reviewMapper;
	
	public int insertMenu(Menu menu) {
		return menuMapper.insertMenu(menu);
	}
	
	public int updateMenu(Menu menu) {
		return menuMapper.updateMenu(menu);
	}
	
	public int deleteMenu(int menuId) {
		return menuMapper.deleteMenu(menuId);
	}
	
	@Transactional
	public Menu getMenu(int menuId) {
//		if (reviewMapper.listViewReview(menuId) == null) {
//			System.out.println("리뷰없음 리셋");
//			menuMapper.updateMenuReset(menuId);
//		} else {
//			System.out.println("리뷰있음 업뎃");
//		}
		menuMapper.addClick(menuId);
		menuMapper.updateMenuGrade(menuId);
		menuMapper.updateMenuTaste(menuId);
		return menuMapper.getMenu(menuId);
	}
	
	//메뉴 가져올때 로그인한 사용자의 최근 본 메뉴에 추가
	@Transactional
	public Menu getMenuCheck(int menuId, String userId) {
		menuMapper.addClick(menuId);
		menuMapper.updateMenuGrade(menuId);
		menuMapper.updateMenuTaste(menuId);
		if(userId!=null) {	
			//String userId = userUtil.getCurrentUserId(); 
			Recent recent =recentMapper.getRecent(userId,menuId); 
			
			if(recent!=null){ //(menuId, userId)가 recent 테이블에 이미 있으면
				
				recentMapper.deleteRecent(recent.getRecentId());
			}
			
			else if(recentMapper.countMyRecent(userId)>=20) { //userId의 최근 본 메뉴가 20개 이상일 경우 가장 오래된 recent 삭제
				Recent tmp = recentMapper.getRecentPast(userId);
				System.out.println(tmp);
				recentMapper.deleteRecent(tmp.getRecentId());
			}

			recent = new Recent();
			recent.setUserId(userId);
			recent.setMenuId(menuId);
			recentMapper.insertRecent(recent);
			return menuMapper.getMenu(menuId);
		}
		else { //로그인 안 된 상태면 그냥 보여주기
			return menuMapper.getMenu(menuId);
		}
	}
	
	public List<Menu> listViewMenu() { 
		return menuMapper.listViewMenu();
	}
	
	public List<Menu> listViewMenuByCondition(String condition) { //condition은 good(좋아요순), click(조회수순)
		return menuMapper.listViewMenuByCondition(condition);
	}
	
	public List<Menu> listViewCafeMenuByCondition(int cafeId, String condition) {
		return menuMapper.listViewCafeMenuByCondition(cafeId, condition);
	}
	
	public List<Menu> listViewCategoryMenuByCondition(int categoryId, String condition) {
		return menuMapper.listViewCategoryMenuByCondition(categoryId, condition);
	}
	
	public List<Menu> listViewCafeMenu(int cafeId) {
		return menuMapper.listViewCafeMenu(cafeId);
	}
	
	public List<Menu> listViewCategoryMenu(int categoryId) {
		return menuMapper.listViewCategoryMenu(categoryId);
	}
	
	public List<Menu> listViewCafeMenuByCategory(int cafeId, int categoryId) {
		return menuMapper.listViewCafeMenuByCategory(cafeId, categoryId);
	}
	
	public List<Menu> listViewCafeCategoryMenuByCondition(int cafeId, int categoryId, String condition) {
		return menuMapper.listViewCafeCategoryMenuByCondition(cafeId, categoryId, condition);
	}
	
	public List<Menu> searchMenu(String keyword) { //메뉴이름, 설명, 키워드
		return menuMapper.searchMenu(keyword);
	}
	
	public List<Menu> searchCafeMenu(int cafeId, String keyword) { //(카페별 검색)메뉴이름, 설명, 키워드
		return menuMapper.searchCafeMenu(cafeId, keyword);
	}
	
	public List<Menu> searchMenuByCondition(String keyword, String condition) { //메뉴이름, 설명, 키워드
		return menuMapper.searchMenuByCondition(keyword, condition);
	}
	
	public List<Menu> searchCafeMenuByCondition(int cafeId, String keyword, String condition) { //(카페별 검색)메뉴이름, 설명, 키워드
		return menuMapper.searchCafeMenuByCondition(cafeId, keyword, condition);
	}
	
	@Transactional
	public List<Menu> listViewRecommendMenuByKeyword(int menuId) {
		String keyword = menuMapper.getMenu(menuId).getKeyword();
		if (keyword != null) {
			String[] list = keyword.split(" ");
			for (String l : list) {
				System.out.println(l);
			}
			return menuMapper.listViewRecommendMenuByKeyword(menuId, list);
		} else {
			return null;
		}
	}
	
	@Transactional
	public List<Menu> listViewRecommendMenuByTaste(int menuId) {
		Taste taste = menuMapper.getMenuTaste(menuId);
		System.out.println(taste);
		List<String> maxCol = new ArrayList<String>();
		if (taste != null) {
			double max = Math.max(Math.max(taste.getSweet(), taste.getBitter()), taste.getSour());
			if (taste.getSweet() == max) 
				maxCol.add("sweet");
			if (taste.getBitter() == max) 
				maxCol.add("bitter");
			if (taste.getSour() == max) 
				maxCol.add("sour");
			System.out.println(maxCol);
			System.out.println(max);
			return menuMapper.listViewRecommendMenuByTaste(menuId, maxCol, max);
		} else {
			return null;
		}
	}

	public List<Menu> listViewLike(String userId) {
		return likesMapper.listViewLike(userId);
	}
	
	public Likes getLike(String userId, int menuId) {
		return likesMapper.getLike(userId, menuId);
	}
	
	@Transactional
	public int insertOrDeleteLike(String userId, int menuId) {
		Likes likes = likesMapper.getLike(userId, menuId);
		if (likes == null) {
			menuMapper.addGood(menuId);
			likes = new Likes();
			likes.setUserId(userId);
			likes.setMenuId(menuId);
			return likesMapper.insertLike(likes);
		} else {
			menuMapper.minusGood(menuId);
			return likesMapper.deleteLike(likes.getLikeId());
		}
	}
	
}
