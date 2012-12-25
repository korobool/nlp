nlp
===

See comprehensive description here https://docs.google.com/document/d/1L53E8pcWIoK_6rQNh_3y4hdN-72VeYqsYtiG2gk4aG8/edit

Java version supports multi-lemma definition, compression, 
and OPTIONALLY you can use primitive Porter Stemmer for unknown words.

Model size reduce is almost implrmrnted, but I don't think that
it is good idea. It would be better to store model on a hard
drive (it is imlemented as tier tree and woldn't be a bottle neck)
