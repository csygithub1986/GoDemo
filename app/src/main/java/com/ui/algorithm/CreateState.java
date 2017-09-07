package com.ui.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CreateState implements IState
{

	private int boardSize;
	private List<ChessPoint> manualList = new ArrayList<ChessPoint>();
	private static int BLACK = 1;
	private static int WHITE = -1;
	private static int Empty = 0;

	public CreateState()
	{};

	public CreateState(String manual)
	{
		setManual(manual);
	}

	@Override
	public void setManual(String manual)
	{
		String[] msg = manual.split(";");
		int sz = msg[1].indexOf("SZ");
		if (msg[1].charAt(sz + 4) >= '0' && msg[1].charAt(sz + 4) <= '9')
		{
			boardSize = Integer.parseInt(msg[1].substring(sz + 3, sz + 5));
		}
		else
		{
			boardSize = Integer.parseInt(msg[1].substring(sz + 3, sz + 4));
		}
		for (int i = 2; i < msg.length; i++)
		{
			if (msg[i].charAt(2) == ']')// 意思是这一手是pass了
			{
				continue;
			}
			int x = msg[i].charAt(2) - 'a';
			int y = msg[i].charAt(3) - 'a';
			manualList.add(new ChessPoint(x >= 0 ? x : x + 58, y >= 0 ? y : y + 58, msg[i].charAt(0) == 'B' ? BLACK : WHITE));
		}
	}

	@Override
	public int getMoveCount()
	{
		return manualList.size();
	}

	@Override
	public int[][] getState(int n)
	{
		if (n > manualList.size() || n < 0)
		{
			n = manualList.size();
		}
		int[][] state = new int[boardSize][boardSize];
		for (int i = 0; i < n; i++)
		{
			ChessPoint p = manualList.get(i);
			state[p.x][p.y] = p.color;
			checkEat(state, p);
		}
		return state;
	}

	// 对刚落子的点四周进行扫描，如果有对方颜色的棋子，判断对方的棋块是否被提子
	private void checkEat(int[][] state, ChessPoint p)
	{
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				int x = p.x + i;
				int y = p.y + j;
				if ((x != p.x && y != p.y) || (x == p.x && y == p.y) || x < 0 || x >= boardSize || y < 0 || y >= boardSize)
				{
					continue;
				}
				if (state[x][y] == -p.color)
				{
					checkBlock(state, new ChessPoint(x, y, -p.color));
				}
			}
		}
	}

	private void checkBlock(int[][] state, ChessPoint p)
	{
		// 这里把ChessPoint用于坐标功能，而不是棋谱信息功能
		List<ChessPoint> tempForeList = new LinkedList<ChessPoint>();
		Set<ChessPoint> tempHisSet = new HashSet<ChessPoint>();
		tempForeList.add(p);
		while (tempForeList.size() != 0)
		{
			if (tempHisSet.contains(tempForeList.get(0)))// 如果在临时历史表里存在
			{
				tempForeList.remove(0);
				continue;
			}
			int pX = tempForeList.get(0).x;
			int pY = tempForeList.get(0).y;
			tempHisSet.add(tempForeList.get(0));// 加入临时历史
			tempForeList.remove(0);// 从待搜列表删除
			// 块搜索
			for (int i = -1; i <= 1; i++)
			{
				for (int j = -1; j <= 1; j++)
				{
					int x = pX + i;
					int y = pY + j;
					if ((x != pX && y != pY) || (x == pX && y == pY) || x < 0 || x >= boardSize || y < 0 || y >= boardSize)
					{
						continue;
					}
					if (state[x][y] == Empty)// 此棋块有气，从这里退出函数，否则while循环结束，棋块被吃
					{
						return;
					}
					else if (state[x][y] == -p.color)// 碰见对方棋子，继续
					{
						continue;
					}
					else if (tempHisSet.contains(new ChessPoint(x, y)) == false &&
								tempForeList.contains(new ChessPoint(x, y)) == false)// 新点不在历史列表和待搜列表中
					{
						tempForeList.add(new ChessPoint(x, y));
					}
				}
			}// end for
		}// end while
			// 将历史列表里的点全部提子
		for (ChessPoint cp : tempHisSet)
		{
			state[cp.x][cp.y] = Empty;
		}
	}

	private static class ChessPoint
	{
		int x;
		int y;
		int color;

		public ChessPoint(int x, int y, int color)
		{
			this.x = x;
			this.y = y;
			this.color = color;
		}

		public ChessPoint(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode()
		{
			// 实际上这里的hashCode用于坐标的判断，并不需要color字段
			return x * 52 + y;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			ChessPoint other = (ChessPoint) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
			
			
		}
	}
}
