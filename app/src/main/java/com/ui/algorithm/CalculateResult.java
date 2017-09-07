package com.ui.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 围棋点目算法
 * @author CSY
 * @version 1.0 2013-3-8
 */

/**
 * @author CSY
 * @version 1.0 2015-3-25
 */
public class CalculateResult
{
	// 棋子颜色
	private static int BLACK = 1;
	private static int WHITE = -1;
	private static int Empty = 0;
	private static int DEADBLACK = 2;
	private static int DEADWHITE = -2;
	private static int BLACKTERRI = 3;
	private static int WHITETERRI = -3;

	// private int[][] state;// 棋盘状态
	private int boardSize;
	// public int[][] FinalState;

	private float blackChessman;
	private float whiteChessman;
	private float blackTerritory;
	private float whiteTerritory;
	private float deadBlack;
	private float deadWhite;
	private float publicTerritory;

	public CalculateResult(int boardSize)
	{
		this.boardSize = boardSize;
	}

	// private int rule;// 中国规则1，日本规则0

	private void init()
	{
		blackChessman = 0;
		whiteChessman = 0;
		blackTerritory = 0;
		whiteTerritory = 0;
		publicTerritory = 0;
		deadBlack = 0;
		deadWhite = 0;
	}

	// public void setChessCHN(int[][] state)
	// {
	// init(state);
	// rule = 1;
	// calculateCHN();
	// }
	//
	// public void setChessJAP(int[][] state, int blackDead, int whiteDead)
	// {
	// init(state);
	// rule = 0;
	// calculateJAP(blackDead, whiteDead);
	// }

	public float getWin(int rule, int[][] state)
	{
		recoveryState(state);
		init();
		calculateTerritory(state);
		if (rule == 1)
		{
			calculateCHN(state);
			return blackChessman + blackTerritory + publicTerritory - boardSize * boardSize * 0.5f;
		}
		else
		{
			calculateJAP(state);
			return blackTerritory + deadWhite - whiteTerritory - deadBlack;
		}
	}

	/**
	 * 数子（中国规则）
	 */
	private void calculateCHN(int[][] state)
	{
		for (int[] is : state)
		{
			for (int i : is)
			{
				if (i == 1) blackChessman++;
				else if (i == -1) whiteChessman++;
			}
		}
	}

	/**
	 * 数死子（日本规则）
	 */
	private void calculateJAP(int[][] state)
	{
		for (int[] is : state)
		{
			for (int i : is)
			{
				if (i == 2) deadBlack++;
				else if (i == -2) deadWhite++;
			}
		}
	}

	/**
	 * 核心的计算函数（求地，遇到对方死子，当成空来点）
	 * 扫描棋盘，遇到气，生出4个领边，如果是气，加入待搜索列表；如果是子，记录第一个颜色，
	 * 并划分领地色，以后的子，如果与第一个不一样，领地所属为公气；最后将此点加入临时搜索历史
	 * 扫描待搜列表中不在临时历史里的点，步骤同上，直到待搜列表为空。
	 * 将地域加到所属色里，并将临时历史加入到总历史。
	 * 扫描棋盘上不在总历史的点，直到完成。
	 */
	private void calculateTerritory(int[][] state)
	{
		Set<Point> globalHisSet = new HashSet<Point>();
		for (int i = 0; i < boardSize; i++)
		{
			for (int j = 0; j < boardSize; j++)
			{
				if (globalHisSet.contains(new Point(i, j)))// 全局历史已经包含此点，则跳过
				{
					continue;
				}
				if (state[i][j] == Empty || state[i][j] == DEADBLACK || state[i][j] == DEADWHITE)// (遇到对方死子，当成空来点，有利于简化程序)
				{
					boolean certain = false;// 已经确定归属
					int whosTerri = Empty;
					List<Point> tempForeList = new LinkedList<Point>();// 将要搜索的列表
					Set<Point> tempHisSet = new HashSet<Point>();
					tempForeList.add(new Point(i, j));// 加入当前点
					// 地域块搜索阶段
					while (tempForeList.size() != 0)
					{
						// if (tempHisSet.contains(tempForeList.get(0)))//
						// 如果在临时历史表里存在?
						// {
						// tempForeList.remove(0);
						// continue;
						// }
						int pX = tempForeList.get(0).getX();
						int pY = tempForeList.get(0).getY();
						tempHisSet.add(tempForeList.get(0));// 加入临时历史
						globalHisSet.add(tempForeList.get(0));// 加入总历史
						tempForeList.remove(0);// 从待搜列表删除
						// 产生新节点
						for (int ix = -1; ix <= 1; ix++)
						{
							for (int iy = -1; iy <= 1; iy++)
							{
								int x = pX + ix;
								int y = pY + iy;
								if ((x != pX && y != pY) || (x == pX && y == pY) || x < 0 || x >= boardSize || y < 0
										|| y >= boardSize)
								{
									continue;
								}
								if (state[x][y] == BLACK || state[x][y] == WHITE)// 如果有子，判定边界
								{
									if (!certain)// 如果还不确定
									{
										whosTerri = state[x][y];
										certain = true;
									}
									else if (whosTerri != state[x][y])
									{
										whosTerri = Empty;
									}
								}
								else if (tempHisSet.contains(new Point(x, y)) == false
										&& tempForeList.contains(new Point(x, y)) == false)// 新点不在历史列表和待搜列表中
								{
									tempForeList.add(new Point(x, y));
								}
							}
						}// end for
					}// end while
					if (whosTerri == BLACK)
					{
						blackTerritory += tempHisSet.size();
						for (Point point : tempHisSet)
						{
							if (state[point.x][point.y] == Empty)
							{
								state[point.x][point.y] = BLACKTERRI;
							}
						}
					}
					else if (whosTerri == WHITE)
					{
						whiteTerritory += tempHisSet.size();
						for (Point point : tempHisSet)
						{
							if (state[point.x][point.y] == Empty)
							{
								state[point.x][point.y] = WHITETERRI;
							}
						}
					}
					else
					{
						publicTerritory += tempHisSet.size() * 0.5f;
					}
				}
			}
		}
	}

	/**
	 * 标注死子
	 * 遇到空或者死子，产生四个新点供搜索，并标注或取消死子
	 */
	public boolean markDead(int[][] state, int i, int j)
	{
		recoveryState(state);
		int from = state[i][j];
		if (state[i][j] == Empty)
		{
			return false;
		}
		int to = 0;
		// -1到-2,1到2的互相转换
		if (from < 0) to = -from - 3;
		else to = -from + 3;

		List<Point> tempForeList = new LinkedList<Point>();// 将要搜索的列表
		Set<Point> tempHisSet = new HashSet<Point>();
		tempForeList.add(new Point(i, j));// 加入当前点
		// 地域块搜索阶段
		while (tempForeList.size() != 0)
		{
			int pX = tempForeList.get(0).getX();
			int pY = tempForeList.get(0).getY();
			if (state[pX][pY] == from)// 改变状态
			{
				state[pX][pY] = to;
				// if (to == DEADBLACK) deadBlack += 1;
				// else if (to == BLACK) deadBlack -= 1;
				// else if (to == DEADWHITE) deadWhite += 1;
				// else deadWhite -= 1;
			}
			tempHisSet.add(tempForeList.get(0));// 加入临时历史
			tempForeList.remove(0);// 从待搜列表删除
			// 产生新节点
			for (int ix = -1; ix <= 1; ix++)
			{
				for (int iy = -1; iy <= 1; iy++)
				{
					int x = pX + ix;
					int y = pY + iy;
					if ((x != pX && y != pY) || (x == pX && y == pY) || x < 0 || x >= boardSize || y < 0
							|| y >= boardSize)
					{
						continue;
					}
					if (state[x][y] == -from || state[x][y] == -to)// 如果是对方的子，或者对方的死子，都停止发散搜索
					{
						continue;
					}

					else if (tempHisSet.contains(new Point(x, y)) == false
							&& tempForeList.contains(new Point(x, y)) == false)// 新点不在历史列表和待搜列表中
					{
						tempForeList.add(new Point(x, y));
					}
				}
			}// end for
		}// end while
		return true;
	}

	private void recoveryState(int[][] state)
	{
		for (int i = 0; i < state.length; i++)
		{
			for (int j = 0; j < state.length; j++)
			{
				if (state[i][j] == 3 || state[i][j] == -3)
				{
					state[i][j] = 0;
				}
			}
		}
	}

	private static class Point
	{
		private int x;
		private int y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int getX()
		{
			return x;
		}

		public int getY()
		{
			return y;
		}

		@Override
		public int hashCode()
		{
			return x * 52 + y;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			Point other = (Point) obj;
			if (x != other.x) return false;
			if (y != other.y) return false;
			return true;
		}
	}
}