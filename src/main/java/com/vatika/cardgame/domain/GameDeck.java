package com.vatika.cardgame.domain;

import java.util.*;
import java.util.random.RandomGenerator;

public class GameDeck {

    private static final Comparator<Card> CARD_ORDER = Comparator.comparing(Card::suit)
            .thenComparing(Card::rank, Comparator.reverseOrder());
    private final List<Card> cards = new ArrayList<>();

    public void addDeck(Deck deck){
        cards.addAll(deck.takeCards());
    }

    public void shuffle(RandomGenerator random){
        for(int i = cards.size()-1; i > 0; i--){
            int j = random.nextInt(i + 1);
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public List<Card> dealCards(int count){
        if(count < 1){
            throw new IllegalArgumentException("Count should be at least 1");
        }
        int n = Math.min(count, cards.size());
        List<Card> dealtList = new ArrayList<>(n);
        for(int i = 0; i < n; i++){
            Card card = cards.removeLast();
            dealtList.add(card);
        }
        return dealtList;
    }

    public Map<Suit, Integer> suitCounts(){
        Map<Suit, Integer> counts = new EnumMap<>(Suit.class);
        for(Suit suit: Suit.values()){
            counts.put(suit, 0);
        }

        for(Card card: cards){
            counts.merge(card.suit(), 1, Integer::sum);
        }
        return counts;
    }

    public Map<Card, Integer> cardCounts(){
        Map<Card, Integer> sorted = new TreeMap<>(CARD_ORDER);
        for(Card card: cards){
            sorted.merge(card, 1, Integer::sum);
        }
        return sorted;
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
}
