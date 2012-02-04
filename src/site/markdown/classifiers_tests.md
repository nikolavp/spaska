#General overview 
Every classifier that wants to be included in spaska should have tests for the standard datasets in the system. We try to keep the quality of the algorithm as high as possible 
and we should not include new code that come without unit tests.

#Writing tests for a new classifier
Tests for the classifiers are really easy to write if you are using some of the standard datasets that are bundled with spaska. This currently includes

* iris
* soybean
* glass
* vote

You just have to subclass our base test class for every classifier - ClassifierTestBase. After extending that class you will have to define

* A logger that will help you throughout the testing
* A classifier object which is just a simple default configuration of your classifier that you want to test
* A test descriptor that will describe what you want to test.

The last one is really interesting by itself - we define a contract by a descriptor that we want the base class to verify. Let's consider a simple example:

We are writing a new classifier for [MaxEnt](http://en.wikipedia.org/wiki/Maximum_entropy_classifier)(consider that this is not yet implemented), so we want to test our classifier on the iris dataset. Here is the code we have to
write for this:

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.10, 0.00);
    }

Here we state the on iris we will have a precision greater than 10% and a recall greater than 0%(note this is just an example). If the results are far greater than this
the base class will warn you that you can make the lower bound greater!

#Testing classifiers
Tests for the classifiers should be run on every change to the API or code in any of the currently included classifiers. The classifiers tests will include restrictive
metric results to disallow unintentional breakage of internal code.
