package com.ui.model;

import android.os.Environment;

import java.io.File;

public class Cst
{
	static
	{
		File skRoot = Environment.getExternalStorageDirectory();
		File file = new File(skRoot, "GoData");
		file.mkdir();
		DataDirectory = file;
	}

	public static File DataDirectory;
}
