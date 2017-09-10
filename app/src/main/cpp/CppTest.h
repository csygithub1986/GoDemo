#pragma once

#include <iostream>
#include<map>
#include <vector>
#include <list>
#include <math.h>
#include "opencv2/opencv.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/cvconfig.h"
#include "opencv/cv.h"
#include "opencv/cxcore.h"
#include "opencv/highgui.h"
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <android/log.h>
//debug用
#define LOG_TAG  "c_debug"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

using namespace cv;
using namespace std;
//using cv::Mat;
//using cv::Vec3f;
//using cv::Point2f;
//using cv::Point;

//#define EX extern "C" __declspec(dllexport)
//#define EX extern "C"
#define GetArrayLen(x) (sizeof((x)) / sizeof(x)[0])


template<class T>
int getArrayLen(T &array) {
    return (sizeof(array) / sizeof(array[0]));
}


//结构体
struct CircleF {
    Point2f Center;
    float Radius;

    CircleF() {}

    CircleF(Point2f center, float radius) {
        Center = center;
        Radius = radius;
    }
};

struct LineSegment2DF {
    Point2f P1;
    Point2f P2;
    //double Length;
    Point2f Direction;

    LineSegment2DF() {}

    LineSegment2DF(Point2f p1, Point2f p2) {
        P1 = p1;
        P2 = p2;
        Direction.x = (p2.x - p1.x) / sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
        Direction.y = (p2.y - p1.y) / sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
    }
};

//枚举
enum CrossType {
    None = 0, Left, Up, Right, Down, LeftUp, RightUp, RightDown, LeftDown, Center
};


//导出函数
bool Detect(unsigned char *, int, int, int, int, int[]);

bool Detect(Mat, int, int, int, int, int[]);

void SetConfig(double, double, double, double, double, double);

void GetCoordinate(int[], int[]);


//内部函数
Mat InitImage(Mat);

vector<CircleF> DetectCircle(Mat uimage, int boardSize);

map<CrossType, list<Point> > DetectCross(uchar *imageBytes, int width, int height);

void LineFit(vector<Point2f> points, Point2f *direction, Point2f *pointOnLine);

Point2f FindLineCross(Point2f direction1, Point2f pointOnLine1, Point2f direction2, Point2f pointOnLine2);

Point *GetGridCoordinate(LineSegment2DF *horizontalLines, LineSegment2DF *verticalLines);

void GetEvenDevideLines(Point2f *conors, Point2f directionLeft, Point2f directionRight, Point2f directionUp, Point2f directionDown, LineSegment2DF *horizontalLines, LineSegment2DF *verticalLines);

Point2f *FindConor(Point2f *directionLeft, Point2f *directionRight, Point2f *directionUp, Point2f *directionDown);

int FindStone(int index, uchar *cannyBytes, uchar *grayImageData);


//测试用。如果是C#，这里面的实现会不一样
void GetCut(Mat bitmap);//手机截屏

void GetOrigin(Mat bitmap);//彩色原图

void GetGrid(Mat *bitmap);//黑白图+网格

void GetCircle(Mat *bitmap);//黑白图+圆
void GetCircle2(void *pixel);//黑白图+圆






