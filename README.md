# HMMTraining
This can be used to train HMM(for cut chinese sentence), generating three probability matrixes:start, transfer and emitter.The three matrixes are generate for "Jieba", in a word, they can be directly used as Python dict or list. \r\n
This project provides three methonds in TrainClass whose constructor need a String type arguement, this arguement is the path of training corpus. Three methods are:
  1.train_with_cutted()  for cutted corpus
  2.train_with_tagged()  if your corpus are tagged
  3.rain_with_raw()  for raw corpus.This method use ICTCLAS50 to cut first.
In this project, every text file is proccessed by a single thread. In this way, it maybe efficient. 
