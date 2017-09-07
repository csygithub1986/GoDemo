package com.ui.algorithm;

public interface ICalculate
{
	// 输入终盘局面，并切换至中国规则
	public void setChessCHN(int[][] state);

	// 输入终盘局面，并切换至中国规则
	public void setChessJAP(int[][] state, int blackDead, int whiteDead);

	// 返回黑棋赢棋的子数或目数，输则返回负数，规则根据用户最后调用的setChess方法
	public float getWin(float compensate);

	// 返回黑棋子数或者目数，规则根据用户最后调用的setChess方法
	public float getBlack();

	// 返回白棋子数或者目数，规则根据用户最后调用的setChess方法
	public float getWhite();
}
