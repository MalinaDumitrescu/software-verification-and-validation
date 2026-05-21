# Pandas Data Analysis and Linear Regression

This repository contains the solution for **documentation_requirements.pdf** of the course **Machine Learning (MASCHINELLE LERNEN)**.
The focus of this lab is **exploratory data analysis (EDA) with Pandas** and a first practical step toward **linear regression** using a real-world dataset.

This repository serves as a foundation for future ML projects, which continue using the same dataset.

---

## Objectives

In this project, you will learn and practice how to:

* Load and explore a real dataset using **Pandas**
* Identify **numerical and categorical features**
* Detect missing values, outliers, and inconsistent data
* Analyze data distributions and relationships using plots
* Communicate insights clearly using visualizations and Markdown
* Apply **simple linear regression** to explore linear relationships between features

---

## Dataset

The project uses the **Ames Housing Dataset**, a well-known dataset frequently used in data science and machine learning experiments.

It contains information about residential properties, including:

* Lot size and area
* Property characteristics
* Construction details
* Sale-related attributes

---

## Tools and Requirements

Make sure you have the following installed:

* **Python 3.11 or newer**
* An IDE with **Jupyter Notebook** support
  (for example, VS Code with the appropriate extensions)
* Python libraries:

  * `pandas`
  * `matplotlib`
  * `scikit-learn`

### Optional (Recommended Good Practice)

* Use a **virtual environment** to manage dependencies
* Use a dependency manager such as **Poetry**
* Install a linter (for example, `pylint`) to improve code quality

These practices are standard in professional and industrial projects.

---

## Exploratory Data Analysis

The analysis includes:

* Loading the CSV dataset into a Pandas DataFrame
* Inspecting the structure and content of the data
* Identifying missing values and potential data quality issues
* Detecting outliers using filters and visual inspection
* Exploring feature distributions using histograms and scatter plots
* Computing descriptive statistics and correlations

Commonly used Pandas and Matplotlib functions include:

* `head`, `describe`
* `isnull`, `isna`, `dropna`
* `value_counts`, `groupby`
* `min`, `max`, `mean`
* `hist`, `scatter`, `corr`

Example of filtering data:

```python
df[df["Lot Area"] > 40000]
```
---

## Communication of Results

The results of the analysis are presented using:

* Clear plots and visualizations
* Markdown cells explaining insights and observations

The goal is not only to analyze the data, but also to **communicate findings clearly**, as expected from a data scientist when working with stakeholders.

---

## Feature Evaluation and Enrichment

The notebook also discusses:

* Which features may not be useful and could be removed
* How the dataset could be enriched with external data
* Potential improvements to data quality and feature relevance

---

## Bonus: Linear Regression

As an extension, the project includes:

* Identification of two features with a potential linear relationship
* Visualization using scatter plots
* Implementation of a **linear regression model** with `scikit-learn`
* Plotting the regression line together with the data points
* Comparing actual values with predicted values for selected samples

---

## Notes
* 
* The same dataset will be reused and extended in upcoming models.
