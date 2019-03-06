package com.lichuan.test01.datamodel;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-9.
 */
public class RootData {
    private ArrayList<Box> mAllBox;
    private ArrayList<Cloth> mAllCloth;
    private ArrayList<Suit> mAllSuit;

    private static RootData mInstance = null;

    public static RootData getInstance() {
        if (mInstance == null) {
            mInstance = new RootData();
        }
        return mInstance;
    }

    private RootData() {
        mAllBox = new ArrayList<Box>();
        mAllCloth = new ArrayList<Cloth>();

        mAllSuit = new ArrayList<Suit>();
    }

    public void reset() {
        mAllCloth = new ArrayList<Cloth>();
        mAllBox = new ArrayList<Box>();

        mAllSuit = new ArrayList<Suit>();
    }

    public void addBox(Box b) {
        mAllBox.add(b);
    }

    public void addCloth(Cloth c) {
        mAllCloth.add(c);
    }

    public void addSuit(Suit s) {
        mAllSuit.add(s);
    }

    public void moveClothIntoBox(Cloth c, Box b) {
        assert (c != null);
        assert (b != null);
        Box source = findClothBelongBox(c);
        if (source != null) {
            source.removeCloth(c);
        }

        b.addCloth(c);
    }

    public int getBoxesCount() {
        return (mAllBox != null ? mAllBox.size() : 0);
    }

    public int getClothesCount() {
        return (mAllCloth != null ? mAllCloth.size() : 0);
    }

    public int getSuitCount() {
        return (mAllSuit != null ? mAllSuit.size() : 0);
    }

    public ArrayList<GridItem> getCloneBoxesAndSomeClothes() {
        ArrayList<GridItem> ret = new ArrayList<GridItem>();
        ret.addAll(getCloneBoxes());

        ArrayList<Cloth> allClothes = getCloneClothes();
        for (Cloth c : allClothes) {
            if (findClothBelongBox(c) == null) {
                ret.add(c);
            }
        }
        return ret;
    }

    public ArrayList<Box> getCloneBoxes() {
        ArrayList<Box> tmp = new ArrayList<Box>();
        if (mAllBox != null && mAllBox.size() > 0) {
            tmp.addAll(mAllBox);
        }
        return tmp;
    }

    public ArrayList<Cloth> getCloneClothes() {
        ArrayList<Cloth> tmp = new ArrayList<Cloth>();
        if (mAllCloth != null && mAllCloth.size() > 0) {
            tmp.addAll(mAllCloth);
        }
        return tmp;
    }

    public ArrayList<Suit> getCloneSuit() {
        ArrayList<Suit> tmp = new ArrayList<Suit>();
        if (mAllSuit != null && mAllSuit.size() > 0) {
            tmp.addAll(mAllSuit);
        }
        return tmp;
    }

    public Box findBox(String name) {
        for (Box b : mAllBox) {
            if (b.getName() != null && b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

    public Cloth findCloth(String name) {
        for (Cloth c : mAllCloth) {
            if (name != null && name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public Box findClothBelongBox(Cloth c) {
        for (Box b : mAllBox) {
            if (b.getClothCount() == 0) {
                continue;
            }
            if (b.containCloth(c)) {
                return b;
            }
        }
        return null;
    }

    public Suit findSuit(String name) {
        for (Suit s : mAllSuit) {
            if (s.getName() != null && s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Suit findClothBelongSuit(Cloth c) {
        for (Suit s : mAllSuit) {
            if (s.getClothCount() == 0) {
                continue;
            }
            if (s.containCloth(c)) {
                return s;
            }
        }
        return null;
    }

    public void removeBox(Box box) {
        if (mAllBox == null) {
            return;
        }
        if (box == null) {
            return;
        }

        for (Box aBox : mAllBox) {
            if (aBox == null) {
                continue;
            }
            if (aBox.getName().equals(box.getName())) {
                mAllBox.remove(aBox);
                return;
            }
        }
    }

    public void removeCloth(Cloth c) {
        if (c == null) {
            return;
        }

        if (mAllCloth == null) {
            return;
        }

        for (Cloth tmp : mAllCloth) {
            if (tmp == null) {
                continue;
            }
            if (tmp.getName().equals(c.getName())) {
                mAllCloth.remove(c);
                return;
            }
        }
    }

    public void removeSuit(Suit s) {
        if (s == null) {
            return;
        }

        if (mAllSuit == null) {
            return;
        }

        for (Suit tmp : mAllSuit) {
            if (tmp == null) {
                continue;
            }

            if (tmp.getName().equals(s.getName())) {
                mAllSuit.remove(s);
                return;
            }
        }
    }
}
