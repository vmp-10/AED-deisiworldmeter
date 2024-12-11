Link do video :) 
https://youtu.be/L19Hrzz5yCU

--------------------------------------
Um log do Chatgpt: 

Vlad:
//sort decrescente, help with ai and intellij
        maioresCidades.sort((c1, c2) -> {
            String ps1 = c1.populacao.substring(0, c1.populacao.length() - 5);
            String ps2 = c2.populacao.substring(0, c2.populacao.length() - 5);

            double pop1 = Double.parseDouble(ps1);
            double pop2 = Double.parseDouble(ps2);

            int comparation = Double.compare(pop2, pop1);
            if (comparation == 0) {
                return c1.nome.compareTo(c2.nome);
            } else {
                return comparation;
            }
        });

Improve this please.

Chatgpt: Here's the improved version:

maioresCidades.sort((c1, c2) -> {
        double pop1 = Double.parseDouble(c1.populacao.substring(0, c1.populacao.length() - POPULATION_SUFFIX_LENGTH));
        double pop2 = Double.parseDouble(c2.populacao.substring(0, c2.populacao.length() - POPULATION_SUFFIX_LENGTH));
        int comparison = Double.compare(pop2, pop1);
        return (comparison == 0) ? c1.nome.compareTo(c2.nome) : comparison;
    });

Vlad: 
"int maxResults \= (numResults == -1) ? maioresCidades.size() : Math.min(numResults, maioresCidades.size());"

Explain this

Chatgpt:
Certainly! Let's break down this line of code:

int maxResults = (numResults == -1) ? maioresCidades.size() : Math.min(numResults, maioresCidades.size());
This line of code determines the maximum number of results that will be displayed based on the input parameter numResults and the number of cities available (maioresCidades.size()).

Here's how it works:

Ternary Operator (?:):

The line starts with a ternary operator (?:). This operator is a shorthand way of writing an if-else statement.
It has the format: condition ? value_if_true : value_if_false.
Condition:

The condition being evaluated is (numResults == -1). This checks if the numResults parameter is equal to -1.
If numResults is -1, it means that the user wants to display all available results without limiting the number of cities.
Value if True:

If the condition (numResults == -1) is true, meaning the user wants to display all available results, then maxResults is assigned the value maioresCidades.size().
maioresCidades.size() returns the total number of cities available for the specified country.
Value if False:

If the condition (numResults == -1) is false, meaning the user wants to display a specific number of results, then maxResults is assigned the minimum of numResults and maioresCidades.size().
Math.min(numResults, maioresCidades.size()) calculates the minimum value between numResults and maioresCidades.size(). This ensures that maxResults does not exceed the total number of available cities.
So, in summary, maxResults will be set to either the total number of available cities (maioresCidades.size()) if numResults is -1, or it will be set to the minimum of numResults and the total number of available cities if numResults is not -1. This ensures that the function displays the correct number of results based on the user's input.


---------------------------------------
