package kr.co.multicafe.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.multicafe.common.utils.UserUtil;
import kr.co.multicafe.dao.LikesMapper;
import kr.co.multicafe.dao.MenuMapper;
import kr.co.multicafe.dao.RecentMapper;
import kr.co.multicafe.dto.Likes;
import kr.co.multicafe.dto.Menu;
import kr.co.multicafe.dto.Recent;

@Service
@Transactional
public class MenuService {
	
	@Autowired
	private MenuMapper menuMapper;
	
	@Autowired
	private LikesMapper likesMapper;
	
	@Autowired
	private RecentMapper recentMapper;
	
	public int insertMenu(Menu menu) {
		return menuMapper.insertMenu(menu);
	}
	
	public int updateMenu(Menu menu) {
		return menuMapper.updateMenu(menu);
	}
	
	public int deleteMenu(int menuId) {
		return menuMapper.deleteMenu(menuId);
	}
	
	public Menu getMenu(int menuId) {
		return menuMapper.getMenu(menuId);
	}
	
	@Transactional
	public Menu getMenuCheck(int menuId) {
		UserUtil userUtil = new UserUtil();
		if(userUtil.getCurrentUsers()!=null) { //로그인이 된 상태면 Recent에 추가하고 Menu 보여주기
			//userId로 recent 가져와서 
			Recent recent = recentMapper.getRecent(menuId);
			recent.setUserId(userUtil.getCurrentUserId());
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
	
	public List<Menu> searchMenu(String keyword) { //메뉴이름, 설명, 키워드
		return menuMapper.searchMenu(keyword);
	}
	
	public List<Menu> searchCafeMenu(int cafeId, String keyword) { //(카페별 검색)메뉴이름, 설명, 키워드
		return menuMapper.searchCafeMenu(cafeId, keyword);
	}

	public List<Menu> listViewLike(String userId) {
		return likesMapper.listViewLike(userId);
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
