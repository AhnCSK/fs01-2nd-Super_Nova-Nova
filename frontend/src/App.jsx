import { Routes, Route } from "react-router-dom";
import "./App.css";
import BasicLayout from "./layouts/layout/BasicLayout";
import Header from "./layouts/header/Header";

import Home from "./pages/Home/Home";
import PlantManage from "./pages/PlantManage/PlantManage";

import MyPage from "./pages/MyPage/MyPage";
import MyPageView from "./pages/MyPage/MyPageView";
import MyPageEdit from "./pages/MyPage/MyPageEdit";
import MyPageTimelapse from "./pages/MyPage/MyPageTimelapse";

import Login from "./pages/Login/Login";
import Signup from "./pages/Login/Signup";

import FindIdPw from "./pages/Login/FindIdPw";
import IDFindPage from "./pages/Login/IDFindPage";
import PWFindVerify from "./pages/Login/PWFindVerify";
import PWFindReset from "./pages/Login/PWFindReset";

import TestHome from "./pages/Login/TestHome";
import AdminHome from "./pages/Login/AdminHome";

// ✅ 여기
import ProtectedRoute from "./api/auth/ProtectedRoute";

function App() {
  return (
    <>
      <Header />

      <Routes>
        {/* 홈 */}
        <Route
          path="/"
          element={
            <BasicLayout>
              <Home />
            </BasicLayout>
          }
        />

        {/* 식물관리 (로그인 필수) */}
        <Route
          path="/plants"
          element={
            <ProtectedRoute>
              <BasicLayout>
                <PlantManage />
              </BasicLayout>
            </ProtectedRoute>
          }
        />

        {/* 마이페이지 (로그인 필수) */}
        <Route
          path="/mypage"
          element={
            <ProtectedRoute>
              <MyPage />
            </ProtectedRoute>
          }
        >
          <Route index element={<MyPageView />} />
          <Route path="view" element={<MyPageView />} />
          <Route path="edit" element={<MyPageEdit />} />
          <Route path="timelapse" element={<MyPageTimelapse />} />
        </Route>

        {/* 로그인 관련 */}
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        {/* ID/PW 찾기 */}
        <Route path="/find" element={<FindIdPw />} />
        <Route path="/find/id" element={<IDFindPage />} />
        <Route path="/find/pw/verify" element={<PWFindVerify />} />
        <Route path="/find/pw/reset" element={<PWFindReset />} />

        {/* 테스트 / 관리자 */}
        <Route path="/wootest" element={<TestHome />} />
        <Route path="/admin" element={<AdminHome />} />
      </Routes>
    </>
  );
}

export default App;
