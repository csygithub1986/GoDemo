package com.ui.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log
{
	public static void PrintError(String msg)
	{
		File file = new File(Cst.DataDirectory, "ChessErrorLog.txt");
		try
		{
			if (file.exists() == false)
			{
				file.createNewFile();
			}
			FileWriter fWriter = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fWriter);
			bw.write(msg);
			fWriter.close();

		}
		catch (IOException e)
		{
			StackTraceElement[] elements = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement stackTraceElement : elements)
			{
				sb.append(stackTraceElement.toString());
			}
			android.util.Log.e("abc", sb.toString());
		}
	}
}
