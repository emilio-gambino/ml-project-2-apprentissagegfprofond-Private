# GFPredictor - EPFL 2022-2023: Machine Learning Project 2

**This project aims at automatically classifying cells based on eGFP levels. For this purpose, we trained a convolutional neural network using PyTorch on amygdala cells from slide scanner images of coronal sections of mice brain. It is made to be used in parallel with [QuPath](https://github.com/qupath/qupath).**

Note: slide scanner images are 4Gb each. Thus we could not include data here but we provided for you the trained model.

## Content of the repository

- GFPredictor.ipynb : Python notebook converting TIF images to PNG and predicting the class of each cell
- GFPTrain.ipynb : Python notebook for training the model
- singleMasks.groovy : Groovy script to use through QuPath
- ImageSep.ipynb : Python notebook we used to split our data in a train and test set
- model.zip : our model that can be used by GFPredictor to make its predictions
- data_transformation.ipynb : Python notebook we used to make the data augmentation
- csv_maker_for_augmented_train.ipynb : Python notebook we used to make csv for augmented train dataset
- report.pdf : the report containing all our findings for this project

## Background

Cell classification from microscope imaging is often a very time-consuming task for many biologists although it is essential when carrying out experiments. Yet, very few performant tools are available and they often need a human verification due to their low reliability. The enhanced Green Fluorescent Protein is one of the most commonly used reporter gene and biologists often need to assess its presence in cells. Unfortunately, due to changing background level across images or even through one image and cells' auto-fluorescence with this protein, automatic classification is hard to implement and current classifiers like threshold-based classifiers still requires human proofreading. In this project we optimized a convolutional neural networks (CNN) reaching an accuracy of over 95% and F1-score of 63% on test set.

## Procedure

### On QuPath

1. Run a cell detection algorithm.

2. Run the script singleMasks.groovy through the script interface that you can find in the panel `Automate`. This will create a folder `cells` with 1 image per cell you want to classify. It will also create a CSV file with the centroids of each cell to be able to map it back to measurement table
 
3. Export your usual measurement table from the `Measure` panel.

### With Python

4. Run the `GFPredictor` Python notebook to make your predictions and add them to your measurement table. This will convert the images to PNG format so it can be read by the model. Then the model will make a prediction for each cell and the prediction will be added to the measurement table by matching each cell with its centroid and image name.

**A column `Prediction` was added to your measurement table with the prediction of the model.**
