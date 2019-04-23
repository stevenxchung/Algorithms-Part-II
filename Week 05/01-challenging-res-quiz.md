## Quiz 1: Challenging REs 

*Construct a regular expression for each of the following languages over the binary alphabet or prove that no such regular expression is possible:*
* All strings except 11 or 111: `^(?!(?:1|111)$)\d+`
* Strings with 1 in every odd-number bit position: `^0*(?:10*10*)*$`
* Strings with an equal number of 0s and 1s: **not possible**
* Strings with at least two 0s and at most one 1: `000* + (000*1 + 010 + 100)0*`
* Strings that when interpreted as a binary integer are a multiple of 3: `(1(01*0)*1|0)*` 
* Strings with no two consecutive 1s: `(1 + ε)(01 + 0)*`
* Strings that are palindromes (same forwards and backwards): **not possible**
* Strings with an equal number of substrings of the form 01 and 10: `0(0+1)*0`