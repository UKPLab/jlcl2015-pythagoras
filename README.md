# Pythagoras

Supervised text classification project using DKPro TC (https://github.com/dkpro/dkpro-tc) to classify the quality of school lessons according to rankings in various dimensions (e.g. constructive feedback). 

Please use the following citation:


```
@article{Flekova:2016:de.tudarmstadt.ukp.experiments.pythagoras,
	author = {Lucie Flekova and Tahir Sousa and Margot Mieskes and Iryna Gurevych},
	title = {Document-level school lesson quality classification based on German
transcripts},
	year = {2015},
	journal = {Journal for Language Technology and Computational Linguistics},
	number = {1},
	pages = {99-124},
	volume = {30},
	issn = {2190-6858}	
}
```
> **Abstract:** This is a copy of my beautiful abstract. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

Contact people: 

* Tahir Sousa, tahirsousa at gmail
* Lucie Flekova, flekova@ukp.informatik.tu-darmstadt.de

http://www.ukp.tu-darmstadt.de/

http://www.tu-darmstadt.de/


Don't hesitate to send us an e-mail or report an issue, if something is broken (and it shouldn't be) or if you have further questions.

> This repository contains experimental software and is published for the sole purpose of giving additional background details on the respective publication. 

## Project structure

* `de.tudarmstadt.ukp.experiments.pythagoras.featureExtractors` -- these packages contain the implementation of feature extractors used in the experiment 
* `de.tudarmstadt.ukp.experiments.pythagoras.wekaProcessor` -- this package contains classification-related processing intended for the WEKA classification framework (http://www.cs.waikato.ac.nz/ml/weka/)

## Requirements

* Java 1.7 and higher
* Maven
* tested on 64-bit Linux versions
* we recommend 4 GB RAM to run the experiments

## Running the experiments
Java Maven project based on DKPro TC framework (see below). 
Set up the environment variable DKPRO_HOME to a target folder where you desire the results to be written.
Then, run the main file PythagorasJavaExperiment.java

To obtain the German lexicons used in particular feature extractors please contact the authors. Lexicon licenses apply.

To obtain the data used for the experiment, please refer to https://www.ukp.tu-darmstadt.de/data/quality-assessment/school-lesson-quality/ 

For detailed description on how to use DKPro TC projects (setup, running, interpreting the results) follow the example demos on the DKPro TC website: https://github.com/dkpro/dkpro-tc.

