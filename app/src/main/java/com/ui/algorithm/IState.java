package com.ui.algorithm;

/**
 * 通过sgf文件格式的字符串状态
 * @author CSY
 * @version 1.0 2013-3-9
 */
public interface IState
{
	// 设置棋谱字符串
	public void setManual(String manual);

	// 获得总的手数
	public int getMoveCount();

	// 获得第n步后棋盘的状态，n从0到开始到总步数
	public int[][] getState(int n);
}
