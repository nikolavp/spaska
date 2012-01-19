[Testing]: classifiers_tests.html
We are following the convention from weka, so if you want to register a new classifier in the system, you should put that classifier in the *spaska.classifiers* package. That's all you have to do so spaska can find your classifier when running the GUI.

When you implement our IClassifier interface which marks your class as a classifier, you will have to implement 3 methods:

* *buildClassifier*<br/>
    Here you have to build the model for the classifier and store it somewhere. The model constructions will depend from the algorithm you are trying to implement.
* *classifyInstance* <br/>
    After you have built the model in buildClassifier you should be able to classify instances depending on their attributes based on the model.
* *getName* <br/>
    This should return a meaningful name of your classifier algorithm.

Consider writing tests for you classifier! Tests for the classifiers are really easy to write in our framework. Look at our [Testing] page for more information.
