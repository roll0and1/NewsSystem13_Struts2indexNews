package com.qiangge.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;
import com.qiangge.dao.NewsDao;
import com.qiangge.model.News;
import com.qiangge.model.NewsModel;
import com.qiangge.utils.AppException;
import com.qiangge.utils.DBUtil;

public class NewsDaoImpl implements NewsDao {
	/**
	 * 添加新闻
	 */
	@Override
	public boolean add(News news) throws AppException {
		// 操作标志
		boolean flag = false;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection(); // 创建数据库连接
		// 声明操作语句：将用户信息保存到数据库中， “？”为占位符
		String sql = "insert into t_news(user_id,newsType_id,title,author,keywords,source,content,createTime,state) values (?,?,?,?,?,?,?,?,?);";
		try {
			// bug
			psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			psmt.setInt(1, news.getUserId());
			psmt.setInt(2, news.getNewsTypeId());
			psmt.setString(3, news.getTitle());
			psmt.setString(4, news.getAuthor());
			psmt.setString(5, news.getKeywords());
			psmt.setString(6, news.getSource());
			psmt.setString(7, news.getContent());
			psmt.setString(8, news.getCreateTime());
			psmt.setInt(9, news.getState());
			// 执行更新
			psmt.executeUpdate();
			// 得到插入行的主键，结果中只有一条记录
			rs = psmt.getGeneratedKeys();
			if (rs.next()) {
				// 获取主键的值并设置到news对象中（添加附件时会用到）
				news.setId(rs.getInt(1));
				flag = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.add");
		} finally {
			DBUtil.closeStatement(psmt);
			DBUtil.closeConnection(conn);
		}

		return flag;
	}

	/**
	 * 获取用户的新闻
	 */
	@Override
	public List<NewsModel> getList(int state, int userId) throws AppException {
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select t_news.id ,t_news.title,t_newstype.name,t_news.createTime,t_news.source "
				+ "from t_news,t_newstype"
				+ " where "
				+ "t_news.state=? and t_news.user_id=? "
				+ "and t_news.newsType_id=t_newstype.id" + " and t_news.del=0;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			psmt.setInt(2, userId);
			rs = psmt.executeQuery();
			// 循环提取结果集中的信息，保存到newList中
			while (rs.next()) {
				NewsModel newsModel = new NewsModel(); // 实例化对象
				newsModel.setId(rs.getInt(1));
				newsModel.setTitle(rs.getString(2));
				newsModel.setNewsType(rs.getString(3));
				// 截取前19个字符 ，否则会显示出“.0”
				String createTime = rs.getString(4).substring(0, 19);
				newsModel.setCreateTime(createTime);
				newsModel.setSource(rs.getString(5));
				newsList.add(newsModel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.getList");
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(psmt);
			DBUtil.closeConnection(conn);
		}
		System.out.println(newsList.size());
		return newsList;
	}

	/**
	 * 获取用户当前页面新闻
	 */

	@Override
	public List<NewsModel> getList(int state, int userId, int currentPage,
			int size) throws AppException {
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select t_news.id ,t_news.title,t_newstype.name,t_news.createTime,t_news.source "
				+ "from t_news,t_newstype"
				+ " where "
				+ "t_news.state=? and t_news.user_id=? "
				+ "and t_news.newsType_id=t_newstype.id"
				+ " and t_news.del=0 "
				+ "limit ?,?;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			psmt.setInt(2, userId);

			// 计算起始位置
			int offset = (currentPage - 1) * size;
			psmt.setInt(3, offset);
			psmt.setInt(4, size);
			rs = psmt.executeQuery();
			// 循环提取结果集中的信息，保存到newList中
			while (rs.next()) {
				NewsModel newsModel = new NewsModel(); // 实例化对象
				newsModel.setId(rs.getInt(1));
				newsModel.setTitle(rs.getString(2));
				newsModel.setNewsType(rs.getString(3));
				// 截取前19个字符 ，否则会显示出“.0”
				String createTime = rs.getString(4).substring(0, 19);
				newsModel.setCreateTime(createTime);
				newsModel.setSource(rs.getString(5));
				newsList.add(newsModel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.getList");
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(psmt);
			DBUtil.closeConnection(conn);
		}
		System.out.println(newsList.size());
		System.out.println("state" + state);
		System.out.println("userId:" + userId);
		System.out.println("currentPage:" + currentPage);
		return newsList;
	}

	@Override
	public int getCount(int state, int userId) throws AppException {
		int count = 0;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();

		String sql = "select count(id) as n from t_news where user_id=? and state=? and del=0;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, userId);
			psmt.setInt(2, state);
			rs = psmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.getCount");
		}
		return count;
	}

	@Override
	public News getNewsById(int id) throws AppException {
		News news = new News();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select author,title,createTime,source,click,content "
				+ "from t_news where id=? and del=0;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, id);
			rs = psmt.executeQuery();
			if (rs.next()) {
				news.setAuthor(rs.getString(1));
				news.setTitle(rs.getString(2));
				news.setCreateTime(rs.getString(3).substring(0, 19));
				news.setSource(rs.getString(4));
				news.setClick(rs.getInt(5));
				news.setContent(rs.getString(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.getRoleById");
		}
		return news;
	}

	@Override
	public List<NewsModel> getList(int state, int currentPage, int size)
			throws AppException {
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select t_news.id ,t_news.title,t_news.createTime,t_newstype.name,t_user.name "
				+ "from t_news,t_newstype,t_user"
				+ " where "
				+ "t_news.state=? "
				+ "and t_news.newsType_id=t_newstype.id"
				+ " and t_user.id=t_news.user_id "
				+ " and t_news.del=0 "
				+ "limit ?,?;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			// 计算起始位置
			int offset = (currentPage - 1) * size;
			psmt.setInt(2, offset);
			psmt.setInt(3, size);
			rs = psmt.executeQuery();
			// 循环提取结果集中的信息，保存到newList中
			while (rs.next()) {
				NewsModel newsModel = new NewsModel(); // 实例化对象
				newsModel.setId(rs.getInt(1));
				newsModel.setTitle(rs.getString(2));
				// 截取前19个字符 ，否则会显示出“.0”
				String createTime = rs.getString(3).substring(0, 19);
				newsModel.setCreateTime(createTime);
				newsModel.setNewsType(rs.getString(4));
				newsModel.setCreator(rs.getString(5));
				newsList.add(newsModel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.getList");
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(psmt);
			DBUtil.closeConnection(conn);
		}
		System.out.println(newsList.size());
		System.out.println("state" + state);
		System.out.println("currentPage:" + currentPage);
		return newsList;
	}

	@Override
	public int getCount(int state) throws AppException {

		int count = 0;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();

		String sql = "select count(id) as n from t_news where state=? and del=0;";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			rs = psmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException("com.qiangge.dao.impl.NewsImpl.getCount");
		}
		return count;
	}

	@Override
	public boolean update(int state, int id) throws AppException {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement psmt = null;
		conn = DBUtil.getConnection();
		String sql = "update t_news set state=? where id=? and del=0";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			psmt.setInt(2, id);
			psmt.executeUpdate();
			flag = true;
		} catch (SQLException e) {
			throw new AppException("com.qiangge.dao.impl.NewsImpl.update");
		}
		return flag;
	}

	@Override
	public List<News> getHotNewsByClick(int state, int num) throws AppException {
		List<News> hotNewsList = new ArrayList<News>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select id,title from t_news where state=? order by click desc limit ? ";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			psmt.setInt(2, num);
			rs = psmt.executeQuery();
			while (rs.next()) {
				News news = new News();
				news.setId(rs.getInt(1));
				news.setTitle(rs.getString(2));
				hotNewsList.add(news);
			}
		} catch (Exception e) {
			throw new AppException(
					"com.qiangge.dao.impl.NewsImpl.getHotNewsByClick");
		}
		return hotNewsList;
	}

	@Override
	public List<News> getlatestNewsByCreateTime(int state, int num)
			throws AppException {
		List<News> hotNewsList = new ArrayList<News>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select id,title from t_news where state=? order by createTime desc limit ? ";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, state);
			psmt.setInt(2, num);
			rs = psmt.executeQuery();
			while (rs.next()) {
				News news = new News();
				news.setId(rs.getInt(1));
				news.setTitle(rs.getString(2));
				hotNewsList.add(news);
			}
		} catch (Exception e) {
			throw new AppException(
					"com.qiangge.dao.impl.NewsImpl.getHotNewsByClick");
		}
		return hotNewsList;
	}

	@Override
	public List<News> getTypeNews(int i, int state, int num)
			throws AppException {
		List<News> typeNewsList = new ArrayList<News>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		conn = DBUtil.getConnection();
		String sql = "select id,title from t_news where newsType_id=? and state=? order by createTime limit ? ";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, i);
			psmt.setInt(2, state);
			psmt.setInt(3, num);
			rs = psmt.executeQuery();
			while (rs.next()) {
				News news = new News();
				
				news.setId(rs.getInt(1));
				news.setTitle(rs.getString(2));
				typeNewsList.add(news);
			}
		} catch (Exception e) {
			throw new AppException(
					"com.qiangge.dao.impl.NewsImpl.getTypeNews");
		}
		System.out.println("typeId"+i+"size："+typeNewsList.size());
		return typeNewsList;
	}
}
