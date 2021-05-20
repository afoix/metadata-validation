package com.afoix.metadatavalidator.validators;

import com.afoix.metadatavalidator.entities.Entity;
import com.afoix.metadatavalidator.exceptions.MisconfiguredValidatorException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * A validator which represents applying a collection of validators in sequence.
 */
public class CompositeValidator extends AbstractValidator implements List<Validator> {

    private final List<Validator> validators = new ArrayList<>();

    public CompositeValidator() {
    }

    public CompositeValidator(Validator... validators) {
        this.addAll(Arrays.asList(validators));
    }

    @Override
    public String getDescription() {
        return getDescriptionOrDefault(() -> "Applies the following validators in sequence: " + String.join(", ", this.stream().map(Validator::getDescription).toList()));
    }

    @Override
    public int size() {
        return validators.size();
    }

    @Override
    public boolean isEmpty() {
        return validators.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return validators.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Validator> iterator() {
        return validators.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return validators.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return validators.toArray(a);
    }

    @Override
    public boolean add(Validator entityTypeValidator) {
        return validators.add(entityTypeValidator);
    }

    @Override
    public boolean remove(Object o) {
        return validators.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return validators.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Validator> c) {
        return validators.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Validator> c) {
        return validators.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return validators.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return validators.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Validator> operator) {
        validators.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Validator> c) {
        validators.sort(c);
    }

    @Override
    public void clear() {
        validators.clear();
    }

    @Override
    public boolean equals(Object o) {
        return validators.equals(o);
    }

    @Override
    public int hashCode() {
        return validators.hashCode();
    }

    @Override
    public Validator get(int index) {
        return validators.get(index);
    }

    @Override
    public Validator set(int index, Validator element) {
        return validators.set(index, element);
    }

    @Override
    public void add(int index, Validator element) {
        validators.add(index, element);
    }

    @Override
    public Validator remove(int index) {
        return validators.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return validators.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return validators.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Validator> listIterator() {
        return validators.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Validator> listIterator(int index) {
        return validators.listIterator(index);
    }

    @NotNull
    @Override
    public List<Validator> subList(int fromIndex, int toIndex) {
        return validators.subList(fromIndex, toIndex);
    }

    @Override
    public void validate(Entity entity, ValidationContext context) throws MisconfiguredValidatorException {
        for (Validator validator :
                this) {
            validator.validate(entity, context);
        }
    }
}
