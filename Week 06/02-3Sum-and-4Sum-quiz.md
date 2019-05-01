## Quiz 2: 3Sum and 4Sum

Consider the following two problems:

* *3Sum* - given an integer array *a*, are there three distinct indices *i*, *j*, and *k* such that *a_i* + *a_j* + *a_k* = 0?
* 4Sum - given an integer array *b*, are there three distinct indices *i*, *j*, *k*, and *l* such that *b_i* + *b_j* + *b_k* + *b_l*= 0?

Show that *3Sum* linear time reduces to *4Sum*

A: If the 3 sum problem, *a_i* + *a_j* + *a_k* = 0 runs in linear time, then the 4 sum problem also runs in linear time *b_i* + *b_j* + *b_k* + *b_l*= 0 provided that there exists distinct integers for 4 sum such that *b_i* + *b_j* + *b_k* + *b_l* = *a_i* + *a_j* + *a_k*