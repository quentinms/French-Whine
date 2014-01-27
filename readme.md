## How it works

### Getting the news

We search Google News for the word ["grève"](http://news.google.com/news?q=greve&hl=fr&num=20&ned=fr) in the french edition which gives us a list of articles (title + newspaper link) that we can fetch as an RSS.

###  Extracting the info

From the list of titles, we try to determine who is on strike. The way we do this is by parsing the title, and then, thanks to a Part-of-Speech tagger, we are able to determine what is the subject, what is the verb, etc. in the sentence. It is then relatively easy to extract the name of the striker with a Regex.

There are however still a few issues: first of all, articles' titles are usually not grammatically correct sentences which sometimes confuses the POS Tagger, and secondly, POS taggers are pretty good in English, but not so much in French.

Finally, the list of strikers is translated by Bing for the English version of the website.

### Libraries used

*   [Play](http://www.playframework.com)
*   [HorroRSS](http://horrorss.googlecode.com) to retrieve the RSS feed
*   [Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml) for the parser and POS tagger.
*   [Microsoft Translator API](https://code.google.com/p/microsoft-translator-java-api/) for the Bing translation

## TODO

*   Better regex to extract useful information
*   Find a more suited POS tagger.
*   Reduce RAM so it can run on free Heroku