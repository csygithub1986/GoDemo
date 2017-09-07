package com.ui.model;

import android.graphics.Point;

public class Coordinater
{
	// 从左上开始顺时针，编号1~4
	public Coordinater(Point p1, Point p2, Point p3, Point p4)
	{
	}

	public Point[][] generate(Point p1, Point p2, Point p3, Point p4)
	{
		int lup = p2.x - p1.x;
		int ldown = p3.x - p4.x;
		int lleft = p4.y - p1.y;
		int lright = p3.y - p2.y;

		Point[][] p = new Point[19][];
		for (int i = 0; i < p.length; i++)
		{
			p[i] = new Point[19];

		}
		return null;
	}
}
