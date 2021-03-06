/**
 * ownCloud Android client application
 *
 * @author purigarcia
 * Copyright (C) 2016 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.test.ui.testSuites;

import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.NoIgnoreTestCategory;
import com.owncloud.android.test.ui.groups.SmokeTestCategory;
import com.owncloud.android.test.ui.models.ElementMenuOptions;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.MoveView;
import com.owncloud.android.test.ui.models.WaitAMomentPopUp;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoveFolderTestSuite {
    AndroidDriver driver;
    Common common;
    private String FOLDER_TO_MOVE = "folderToMove";
    private String FOLDER_WHERE_MOVE = "folderWhereMove";

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() throws Exception {
        common = new Common();
        driver = common.setUpCommonDriver();
    }

    @Test
    @Category({NoIgnoreTestCategory.class, SmokeTestCategory.class})
    public void testMoveFolder() throws Exception {
        WaitAMomentPopUp waitAMomentPopUp;

        FileListView fileListView = Actions.login(Config.URL, Config.user,
                Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();

        //Common.waitTillElementIsNotPresentWithoutTimeout(
        //fileListView.getProgressCircular(), 1000);

        //check if the folder already exists and if true, delete them
        Actions.deleteElement(FOLDER_WHERE_MOVE, fileListView, driver);
        Actions.deleteElement(FOLDER_TO_MOVE, fileListView, driver);

        //Create the folder where the other is gone to be moved
        waitAMomentPopUp = Actions
                .createFolder(FOLDER_WHERE_MOVE, fileListView);
        Common.waitTillElementIsNotPresentWithoutTimeout(
                waitAMomentPopUp.getWaitAMomentTextElement(), 100);
        fileListView.scrollTillFindElement(FOLDER_WHERE_MOVE);
        assertTrue(fileListView.getFileElement().isDisplayed());

        //Create the folder which is going to be moved
        waitAMomentPopUp = Actions.createFolder(FOLDER_TO_MOVE, fileListView);
        Common.waitTillElementIsNotPresent(
                waitAMomentPopUp.getWaitAMomentTextElement(), 100);
        fileListView.scrollTillFindElement(FOLDER_TO_MOVE);
        assertTrue(fileListView.getFileElement().isDisplayed());

        //select to move the folder
        ElementMenuOptions menuOptions = fileListView
                .longPressOnElement(FOLDER_TO_MOVE);
        MoveView moveView = menuOptions.clickOnMove();

        //to move to a folder
        moveView.scrollTillFindElement(FOLDER_WHERE_MOVE).tap(1, 1);
        waitAMomentPopUp = moveView.clickOnChoose();
        Common.waitTillElementIsNotPresentWithoutTimeout(waitAMomentPopUp
                .getWaitAMomentTextElement(), 100);

        //check that the folder moved is inside the other
        fileListView.scrollTillFindElement(FOLDER_WHERE_MOVE).tap(1, 1);
        Common.waitTillElementIsNotPresentWithoutTimeout(fileListView.getProgressCircular(),
                1000);
        Thread.sleep(1000);
        fileListView.scrollTillFindElement(FOLDER_TO_MOVE);
        assertEquals(FOLDER_TO_MOVE, fileListView.getFileElement().getText());
    }

    @After
    public void tearDown() throws Exception {
        common.takeScreenShotOnFailed(name.getMethodName());
        FileListView fileListView = new FileListView(driver);
        driver.sendKeyEvent(android.view.KeyEvent.KEYCODE_BACK);
        Actions.deleteElement(FOLDER_WHERE_MOVE, fileListView, driver);
        Actions.deleteElement(FOLDER_TO_MOVE, fileListView, driver);
        driver.removeApp("com.owncloud.android");
        driver.quit();
    }

}
