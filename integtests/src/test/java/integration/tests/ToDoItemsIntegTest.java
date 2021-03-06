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
package integration.tests;

import dom.todo.ToDoItem;
import dom.todo.ToDoItems;
import fixture.todo.integtests.ToDoItemsIntegTestFixture;

import java.util.List;
import javax.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ToDoItemsIntegTest extends AbstractToDoIntegTest {

    @Inject
    ToDoItems toDoItems;

    public static class Finders extends ToDoItemsIntegTest {

        @Before
        public void setUpData() throws Exception {
            scenarioExecution().install(new ToDoItemsIntegTestFixture());
        }

        private int notYetCompletedSize;
        private int completedSize;

        @Before
        public void setUp() throws Exception {
            final List<ToDoItem> notYetCompleteItems = wrap(toDoItems).notYetComplete();
            final List<ToDoItem> completedItems = wrap(toDoItems).complete();

            notYetCompletedSize = notYetCompleteItems.size();
            completedSize = completedItems.size();

            assertThat(notYetCompletedSize, is(Matchers.greaterThan(5)));
        }

        @Test
        public void complete_and_notYetComplete() throws Exception {

            // given
            List<ToDoItem> notYetCompleteItems = wrap(service(ToDoItems.class)).notYetComplete();
            final ToDoItem toDoItem = wrap(notYetCompleteItems.get(0));

            // when
            toDoItem.completed();

            // then
            assertThat(wrap(service(ToDoItems.class)).notYetComplete().size(), is(notYetCompletedSize-1));
            assertThat(wrap(service(ToDoItems.class)).complete().size(), is(completedSize+1));

            // and when
            toDoItem.notYetCompleted();

            // then
            assertThat(wrap(service(ToDoItems.class)).notYetComplete().size(), is(notYetCompletedSize));
            assertThat(wrap(service(ToDoItems.class)).complete().size(), is(completedSize));
        }
    }

    public static class NewToDo_and_Delete extends ToDoItemsIntegTest {

        @Before
        public void setUpData() throws Exception {
            // none
        }

        @Test
        public void happyCase() throws Exception {

            // given
            int size = wrap(toDoItems).notYetComplete().size();

            // when
            final ToDoItem newToDo = toDoItems.newToDo("new todo", ToDoItem.Category.Professional, ToDoItem.Subcategory.OpenSource, null, null);

            // then
            assertThat(newToDo.getDescription(), is("new todo"));
            assertThat(newToDo.getCategory(), is(ToDoItem.Category.Professional));
            assertThat(wrap(service(ToDoItems.class)).notYetComplete().size(), is(size+1));

            // when
            newToDo.delete();

            // then
            assertThat(wrap(service(ToDoItems.class)).notYetComplete().size(), is(size));
        }

    }

}