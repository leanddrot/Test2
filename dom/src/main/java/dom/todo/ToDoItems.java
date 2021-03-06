/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package dom.todo;

import dom.todo.ToDoItem.Category;
import dom.todo.ToDoItem.Subcategory;

import java.math.BigDecimal;
import java.util.List;
import com.google.common.base.Predicates;
import org.joda.time.LocalDate;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;

@Named("ToDos")
@DomainService(menuOrder = "10", repositoryFor = ToDoItem.class)
public class ToDoItems {

    //region > identification in the UI
    public String getId() {
        return "toDoItems";
    }

    public String iconName() {
        return "ToDoItem";
    }
    //endregion

    //region > notYetComplete (action)
    @Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "10")
    public List<ToDoItem> notYetComplete() {
        final List<ToDoItem> items = notYetCompleteNoUi();
        if(items.isEmpty()) {
            container.informUser("All to-do items have been completed :-)");
        }
        return items;
    }

    @Programmatic
    public List<ToDoItem> notYetCompleteNoUi() {
        return container.allMatches(
                new QueryDefault<ToDoItem>(ToDoItem.class, 
                        "findByOwnedByAndCompleteIsFalse", 
                        "ownedBy", currentUserName()));
    }
    //endregion

    //region > complete (action)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "20")
    public List<ToDoItem> complete() {
        final List<ToDoItem> items = completeNoUi();
        if(items.isEmpty()) {
            container.informUser("No to-do items have yet been completed :-(");
        }
        return items;
    }

    @Programmatic
    public List<ToDoItem> completeNoUi() {
        return container.allMatches(
            new QueryDefault<ToDoItem>(ToDoItem.class, 
                    "findByOwnedByAndCompleteIsTrue", 
                    "ownedBy", currentUserName()));
    }
    //endregion

    //region > categorized (action)
	@SuppressWarnings("unchecked")
	@Bookmarkable
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "40")
    public List<ToDoItem> categorized(
    		@Named("Category") final Category category,
    		@Named("Subcategory") final Subcategory subcategory,
    		@Named("Completed?") final boolean completed) {
    	// an example "naive" implementation (filtered in Java code, not DBMS)
        return container.allMatches(ToDoItem.class, 
                Predicates.and(
                    ToDoItem.Predicates.thoseOwnedBy(currentUserName()), 
                    ToDoItem.Predicates.thoseCompleted(completed),
                    ToDoItem.Predicates.thoseCategorised(category, subcategory)));
    }
    public Category default0Categorized() {
        return Category.Professional;
    }
    public Subcategory default1Categorized() {
        return default0Categorized().subcategories().get(0);
    }
    public boolean default2Categorized() {
    	return false;
    }
    public List<Subcategory> choices1Categorized(
            final Category category) {
        return Subcategory.listFor(category);
    }
    public String validateCategorized(
            final Category category, 
            final Subcategory subcategory, 
            final boolean completed) {
        return Subcategory.validate(category, subcategory);
    }
    //endregion

    //region > newToDo (action)
    @MemberOrder(sequence = "5")
    public ToDoItem newToDo(
            final @RegEx(validation = "\\w[@&:\\-\\,\\.\\+ \\w]*") @Named("Description") String description, 
            final @Named("Category") Category category,
            final @Optional @Named("Subcategory") Subcategory subcategory,
            final @Optional @Named("Due by") LocalDate dueBy,
            final @Optional @Named("Cost") BigDecimal cost) {
        return newToDo(description, category, subcategory, currentUserName(), dueBy, cost);
    }
    public Category default1NewToDo() {
        return Category.Professional;
    }
    public Subcategory default2NewToDo() {
        return Category.Professional.subcategories().get(0);
    }
    public LocalDate default3NewToDo() {
        return clockService.now().plusDays(14);
    }
    public List<Subcategory> choices2NewToDo(
            final String description, final Category category) {
        return Subcategory.listFor(category);
    }
    public String validateNewToDo(
            final String description, 
            final Category category, final Subcategory subcategory, 
            final LocalDate dueBy, final BigDecimal cost) {
        return Subcategory.validate(category, subcategory);
    }
    //endregion

    //region > allToDos (action)
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "50")
    public List<ToDoItem> allToDos() {
        final List<ToDoItem> items = container.allMatches(
                new QueryDefault<ToDoItem>(ToDoItem.class, 
                        "findByOwnedBy", 
                        "ownedBy", currentUserName()));
        if(items.isEmpty()) {
            container.warnUser("No to-do items found.");
        }
        return items;
    }
    //endregion

    //region > autoComplete (programmatic)
    @Programmatic // not part of metamodel
    public List<ToDoItem> autoComplete(final String description) {
        return container.allMatches(
                new QueryDefault<ToDoItem>(ToDoItem.class, 
                        "findByOwnedByAndDescriptionContains", 
                        "ownedBy", currentUserName(), 
                        "description", description));
    }
    //endregion

    //region > helpers
    @Programmatic // for use by fixtures
    public ToDoItem newToDo(
            final String description, 
            final Category category, 
            final Subcategory subcategory,
            final String userName, 
            final LocalDate dueBy, final BigDecimal cost) {
        final ToDoItem toDoItem = container.newTransientInstance(ToDoItem.class);
        toDoItem.setDescription(description);
        toDoItem.setCategory(category);
        toDoItem.setSubcategory(subcategory);
        toDoItem.setOwnedBy(userName);
        toDoItem.setDueBy(dueBy);
        toDoItem.setCost(cost);

        container.persist(toDoItem);
        container.flush();

        return toDoItem;
    }
    
    private String currentUserName() {
        return container.getUser().getName();
    }

    //endregion

    //region > common validation
    private static final long ONE_WEEK_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    @Programmatic
    public String validateDueBy(LocalDate dueBy) {
        return isMoreThanOneWeekInPast(dueBy) ? "Due by date cannot be more than one week old" : null;
    }
    @Programmatic
    boolean isMoreThanOneWeekInPast(final LocalDate dueBy) {
        return dueBy.toDateTimeAtStartOfDay().getMillis() < clockService.nowAsMillis() - ONE_WEEK_IN_MILLIS;
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    private ClockService clockService;
    //endregion

}
