package com.qiangge.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.qiangge.model.News;
import com.qiangge.model.PageModel;
import com.qiangge.service.NewsService;
import com.qiangge.utils.AppException;

public class NewsAction extends ActionSupport {
	private News news;
	private int id;

	NewsService newsService = new NewsService();

	private int state = 0; // 默认新闻状态

	private PageModel pageModel;

	private int currentPage = 1;
	private int size = 5;

	private List<News> hotNewsList; // 热门新闻
	private List<News> latestNewsList;// 最新新闻

	/** 栏目新闻 **/
	private List<News> internationalNewsList; // 国际新闻
	private List<News> domesticNewsList;// 国内新闻

	private List<News> sportsNewsList;// 体育新闻

	private List<News> entertainmentNewsList;// 娱乐新闻

	private List<News> autoNewsList;// 汽车新闻

	private List<News> financialNewsList;// 财经新闻

	public String check() {
		// 初始化session
		Map<String, Object> session = ActionContext.getContext().getSession();
		// 设置标签位
		boolean flag = false;
		// 获取用户信息
		Integer userId = (Integer) session.get("userId");
		// 判断用户是否登录
		if (null == userId) {
			// 用户未登录，重定向到login
			return "login";
		}
		try {
			// 获取当前用户创建的新闻信息
			flag = newsService.check(state, id);
			if (flag) {
				// 重定向到toUncheck
				return "toUncheck";
			} else {
				return "error";
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 跳转到错误页面
			return "error";
		}

	}

	public String create() {
		String message = null;
		HttpServletRequest request = null;
		// 获取userId
		Map<String, Object> session = ActionContext.getContext().getSession();
		int userId = (int) session.get("userId");
		String name = (String) session.get("name");
		// 验证用户是否登录
		if (null == name) {
			return "login";
		}

		news.setUserId(userId);

		news.setState(0); // 设置状态为"未审核"

		try {
			boolean flag = newsService.create(news);
			if (flag) {
				message = "创建成功";
			} else {
				message = "创建失败";
			}
			// 将message和news对象存入到request中
			// 获取request
			request = (HttpServletRequest) ActionContext.getContext().get(
					ServletActionContext.HTTP_REQUEST);
			// 将message和news对象存入request
			request.setAttribute("message", message);
			request.setAttribute("news", news);
			return "create";

		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 重定向到错误页面
			return "error";
		}
	}

	public List<News> getAutoNewsList() {
		return autoNewsList;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public List<News> getDomesticNewsList() {
		return domesticNewsList;
	}

	public List<News> getEntertainmentNewsList() {
		return entertainmentNewsList;
	}

	public List<News> getFinancialNewsList() {
		return financialNewsList;
	}

	public List<News> getHotNewsList() {
		return hotNewsList;
	}

	public int getId() {
		return id;
	}

	public List<News> getInternationalNewsList() {
		return internationalNewsList;
	}

	public List<News> getLatestNewsList() {
		return latestNewsList;
	}

	public News getNews() {
		return news;
	}

	public NewsService getNewsService() {
		return newsService;
	}

	public PageModel getPageModel() {
		return pageModel;
	}

	public int getSize() {
		return size;
	}

	public List<News> getSportsNewsList() {
		return sportsNewsList;
	}

	public int getState() {
		return state;
	}

	public String myNews() {
		// 初始化session
		Map<String, Object> session = ActionContext.getContext().getSession();
		// 获取用户信息
		Integer userId = (Integer) session.get("userId");
		// 判断用户是否登录
		if (null == userId) {
			// 用户未登录，重定向到login
			return "login";
		}
		try {
			// 获取当前用户创建的新闻信息
			pageModel = newsService.getList(state, userId, currentPage, size);
			// // 将newsList存入request
			Map<String, Object> request = (Map<String, Object>) ActionContext
					.getContext().get("request");
			request.put("pageModel", pageModel);
			return "myNews";
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 跳转到错误页面
			return "error";
		}

	}

	public String preview() {
		// 判断是是否登录
		Map<String, Object> session = ActionContext.getContext().getSession();
		Integer userId = (Integer) session.get("userId");
		if (null == userId) {
			return "login";
		}

		News news = null;
		try {
			news = newsService.preview(id);
			// 将news存入request
			Map<String, Object> request = (Map<String, Object>) ActionContext
					.getContext().get("request");
			request.put("news", news);
			return "preview";
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}

	public void setAutoNewsList(List<News> autoNewsList) {
		this.autoNewsList = autoNewsList;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setDomesticNewsList(List<News> domesticNewsList) {
		this.domesticNewsList = domesticNewsList;
	}

	public void setEntertainmentNewsList(List<News> entertainmentNewsList) {
		this.entertainmentNewsList = entertainmentNewsList;
	}

	public void setFinancialNewsList(List<News> financialNewsList) {
		this.financialNewsList = financialNewsList;
	}

	public void setHotNewsList(List<News> hotNewsList) {
		this.hotNewsList = hotNewsList;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInternationalNewsList(List<News> internationalNewsList) {
		this.internationalNewsList = internationalNewsList;
	}

	public void setLatestNewsList(List<News> latestNewsList) {
		this.latestNewsList = latestNewsList;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public void setPageModel(PageModel pageModel) {
		this.pageModel = pageModel;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setSportsNewsList(List<News> sportsNewsList) {
		this.sportsNewsList = sportsNewsList;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String toCreate() {
		// 获取session
		Map<String, Object> session = ActionContext.getContext().getSession();
		String name = (String) session.get("name");
		if (null == name) {
			return "login";
		} else {
			return "create";
		}
	}

	public String toUncheck() {
		// 初始化session
		Map<String, Object> session = ActionContext.getContext().getSession();
		// 获取用户信息
		Integer userId = (Integer) session.get("userId");
		// 判断用户是否登录
		if (null == userId) {
			// 用户未登录，重定向到login
			return "login";
		}
		try {
			// 获取当前用户创建的新闻信息
			pageModel = newsService.getList(state, currentPage, size);
			// 将newsList存入request
			Map<String, Object> request = (Map<String, Object>) ActionContext
					.getContext().get("request");
			request.put("newsPageModel", pageModel);
			return "check";
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 跳转到错误页面
			return "error";
		}

	}

	public String index() {
		
		int num = 10;
		state = 1;
		try {
			hotNewsList = newsService.getHotNews(state, num);
			latestNewsList = newsService.getLatestNews(state, num);
			internationalNewsList = newsService.getNewsByType(1, state, num);
			domesticNewsList = newsService.getNewsByType(2, state, num);
			entertainmentNewsList = newsService.getNewsByType(3, state, num);
			sportsNewsList = newsService.getNewsByType(4, state, num);
			financialNewsList = newsService.getNewsByType(5, state, num);
			autoNewsList = newsService.getNewsByType(6, state, num);

			// 将newsList存入request
			Map<String, Object> request = (Map<String, Object>) ActionContext
					.getContext().get("request");
			request.put("hotNewsList", hotNewsList);
			request.put("latestNewsList", latestNewsList);
			request.put("internationalNewsList", internationalNewsList);
			request.put("domesticNewsList", domesticNewsList);
			request.put("entertainmentNewsList", entertainmentNewsList);
			request.put("sportsNewsList", sportsNewsList);
			request.put("financialNewsList", financialNewsList);
			request.put("autoNewsList", autoNewsList);
			return "index";
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}
}
