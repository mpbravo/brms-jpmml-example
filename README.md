# brms-jpmml-example
Shows how to process PMML models using JPMML

In this test, the goal is to be able to launch a PMML model using JPMML
The JPMML version used is 1.2.

 * NOTE: There is a conflict between Google Guava library versions in JBoss BRMS 6.1 and JPMML evaluator.
The first uses version 13.0.1 while the last needs at least 14.0.1.

 * NOTE: JPMML does not support functionName="mixed"

functionName: Could be either "classification" or "regression" depending on the target variable(s). 
If the model contains categorical and continuous targets, this attribute should be "mixed". 
In case no targets are present then it should be "clustering". See  <http://www.dmg.org/v4-2-1/KNN.html>

System Requirements
-------------------

 * Maven

Running the example
-------------------

        mvn test
