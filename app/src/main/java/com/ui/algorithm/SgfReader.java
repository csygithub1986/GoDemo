package com.ui.algorithm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SgfReader
{
	public static int[][] readFile(File file) throws IOException
	{
		System.out.println("请输入sgf棋谱名称，不带后缀名；输入exit退出：");
		FileReader fr;
		try
		{
			fr = new FileReader(file);
		}
		catch (Exception e)
		{
			System.out.println("无此文件，请重新输入");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int r;
		while ((r = fr.read()) != -1)
		{
			sb.append((char) r);
		}
		fr.close();

		IState c = new CreateState(sb.toString());
		int[][] state = c.getState(c.getMoveCount());// 获得第几步的状态

		return state;
	}
}
