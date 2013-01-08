nlp
===

See comprehensive description here:
http://korobov-labs.blogspot.com/2013/01/supervised-learning-model-for.html
http://korobov-labs.blogspot.com/2012/12/blog-post_26.html

Java version supports multi-lemma definition, compression, 
and OPTIONALLY you can use primitive Porter Stemmer for unknown words.

Model size reduce is almost implrmrnted, but I don't think that
it is good idea. It would be better to store model on a hard
drive (it is imlemented as tier tree and woldn't be a bottle neck)
