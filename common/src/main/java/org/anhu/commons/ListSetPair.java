package org.anhu.commons;

import java.util.*;

public class ListSetPair<K> {

    public final List<K> list;
    public final Set<K> set;

    public ListSetPair(){
        list = new LinkedList<>();
        set = new HashSet<>();
    }

    public ListSetPair(List<K> list, Set<K> set){
        this.list = list;
        this.set = set;
    }

    public ListSetPair(Collection<K> collection){
        list = new LinkedList<>(collection);
        set = new HashSet<>(collection);
    }

    public boolean contains(K item){
        return set.contains(item);
    }

    public void addIfNotPresent(K item){
        if(set.add(item)){
            list.add(item);
        }
    }

    public void addAll(Collection<K> collection){
        for(K k : collection){
            if(set.add(k)){
                list.add(k);
            }
        }
    }

    public boolean removeIfPresent(K item){
        if(set.remove(item)){
            list.remove(item);
            return true;
        }
        return false;
    }
}
