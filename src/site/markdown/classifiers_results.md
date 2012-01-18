The classifiers in spaska are doing really well on most datasets. We currently run all classifiers on iris, vote and soybean datasets to test the classifiers implementations.  Every classifier states what is the *lower* bound of the recall and the precision that's reached. This way if someone is making improvements/optimizations to the classifier, he can be sure that he is not lowering the general/overall results. The current results were generated from a script in our scripts project directory.

<table border="1"><thead>
            <th>classname</th><th>dataset</th><th>recall</th><th>precision</th>
            <tr><td>spaska.classifiers.DecisionTreeTest</td><td>soybean</td><td>0.6830034382093879</td><td>0.7032423839061229</td></tr>
<tr><td>spaska.classifiers.DecisionTreeTest</td><td>vote</td><td>0.9165997859818085</td><td>0.8985624706735487</td></tr>
<tr><td>spaska.classifiers.DecisionTreeTest</td><td>iris</td><td>0.94</td><td>0.9409875551987154</td></tr>
<tr><td>spaska.classifiers.KNNTest</td><td>iris</td><td>0.9666666666666667</td><td>0.9667867146858743</td></tr>
<tr><td>spaska.classifiers.KNNTest</td><td>vote</td><td>0.9185727661851257</td><td>0.907136351808483</td></tr>
<tr><td>spaska.classifiers.KNNTest</td><td>soybean</td><td>0.9339053395117468</td><td>0.9605852473578264</td></tr>
<tr><td>spaska.classifiers.NaiveBayesTest</td><td>soybean</td><td>0.9211555035353661</td><td>0.9542788129744653</td></tr>
<tr><td>spaska.classifiers.NaiveBayesTest</td><td>vote</td><td>0.9223180845371857</td><td>0.9120481141514769</td></tr>
<tr><td>spaska.classifiers.NaiveBayesTest</td><td>glass</td><td>0.2506166895070188</td><td>0.225993850993851</td></tr>
<tr><td>spaska.classifiers.NaiveBayesTest</td><td>iris</td><td>0.9533333333333333</td><td>0.9534480458850206</td></tr>
<tr><td>spaska.classifiers.OneRTest</td><td>iris</td><td>0.6533333333333333</td><td>0.6296296296296297</td></tr>
<tr><td>spaska.classifiers.OneRTest</td><td>vote</td><td>0.9589018191546281</td><td>0.9507620549205098</td></tr>
<tr><td>spaska.classifiers.OneRTest</td><td>soybean</td><td>0.051600573339703776</td><td>0.013970516701670453</td></tr>
<tr><td>spaska.classifiers.TwoLayerPerceptronTest</td><td>soybean</td><td>0.8994302322391566</td><td>0.9096192347938703</td></tr>
<tr><td>spaska.classifiers.TwoLayerPerceptronTest</td><td>vote</td><td>0.9455591225254147</td><td>0.9475746853585345</td></tr>
<tr><td>spaska.classifiers.TwoLayerPerceptronTest</td><td>iris</td><td>0.9466666666666667</td><td>0.9466666666666667</td></tr>
<tr><td>spaska.classifiers.ZeroRTest</td><td>vote</td><td>0.5</td><td>0.30689655172413793</td></tr>
<tr><td>spaska.classifiers.ZeroRTest</td><td>iris</td><td>0.3333333333333333</td><td>0.1111111111111111</td></tr>
<tr><td>spaska.classifiers.ZeroRTest</td><td>soybean</td><td>0.05263157894736842</td><td>0.007012406565461971</td></tr>
            </thead></table>
