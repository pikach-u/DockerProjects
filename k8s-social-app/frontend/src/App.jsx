import React, { useState, useEffect } from "react";
import axios from "axios";
import "./App.css";

/**
 * 메인 App 컴포넌트
 * React Hooks(useState, useEffect)를 사용하여 상태 관리
 */
const App = () => {
  // 상태 관리를 위한 React Hooks
  const [posts, setPosts] = useState([]); // 게시글 목록
  const [title, setTitle] = useState(""); // 게시글 제목
  const [content, setContent] = useState(""); // 게시글 내용
  const [author, setAuthor] = useState(""); // 작성자
  const [loading, setLoading] = useState(false); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태

  // 환경에 따른 API URL 설정
  // 운영환경에서는 nginx를 통해 /api로 프록시되고, 개발환경에서는 package.json의 proxy 설정 사용
  const API_URL = process.env.NODE_ENV === "production" ? "/api" : "/api";

  // 컴포넌트 마운트 시 게시글 목록 로드
  useEffect(() => {
    fetchPosts();
  }, []);

  /**
   * 서버에서 게시글 목록을 가져오는 함수
   */
  const fetchPosts = async () => {
    try {
      setLoading(true);
      setError(null);

      console.log("Fetching posts from:", `${API_URL}/posts`);
      const response = await axios.get(`${API_URL}/posts`);

      console.log("Posts fetched successfully:", response.data);

      // Page 객체에서 content 배열 추출
      if (response.data && response.data.content) {
        setPosts(response.data.content);
      } else if (Array.isArray(response.data)) {
        setPosts(response.data);
      } else {
        setPosts([]);
        console.warn("Unexpected response format:", response.data);
      }
    } catch (error) {
      console.error("Error fetching posts:", error);
      setError("게시글을 불러오는 중 오류가 발생했습니다.");

      // 네트워크 오류 상세 정보 로깅
      if (error.response) {
        console.error(
          "Response error:",
          error.response.status,
          error.response.data
        );
      } else if (error.request) {
        console.error("Request error:", error.request);
      } else {
        console.error("Error:", error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * 새 게시글 작성 폼 제출 처리
   */
  const handleSubmit = async (e) => {
    e.preventDefault(); // 폼의 기본 제출 동작 방지

    // 입력값 검증
    if (!title.trim() || !content.trim() || !author.trim()) {
      setError("모든 필드를 입력해주세요.");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const postData = {
        title: title.trim(),
        content: content.trim(),
        author: author.trim(),
      };

      console.log("Creating post:", postData);
      const response = await axios.post(`${API_URL}/posts`, postData);

      console.log("Post created successfully:", response.data);

      // 폼 초기화
      setTitle("");
      setContent("");
      setAuthor("");

      // 게시글 목록 새로고침
      await fetchPosts();

      alert("게시글이 성공적으로 등록되었습니다!");
    } catch (error) {
      console.error("Error creating post:", error);

      if (error.response && error.response.data && error.response.data.error) {
        setError(error.response.data.error);
      } else {
        setError("게시글 등록 중 오류가 발생했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * 날짜 포맷팅 함수
   */
  const formatDate = (dateString) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch (error) {
      return "날짜 정보 없음";
    }
  };

  return (
    <div className="app">
      <div className="container">
        <header className="header">
          <h1>📱 Simple Social</h1>
          <p>스프링부트 + 리액트로 만든 소셜 앱</p>
        </header>

        {/* 에러 메시지 표시 */}
        {error && (
          <div className="error-message">
            <p>⚠️ {error}</p>
            <button onClick={() => setError(null)} className="error-close">
              닫기
            </button>
          </div>
        )}

        {/* 게시글 작성 폼 */}
        <section className="post-form-section">
          <h2>✍️ 새 게시글 작성</h2>
          <form onSubmit={handleSubmit} className="post-form">
            <div className="form-group">
              <label htmlFor="author">작성자</label>
              <input
                id="author"
                type="text"
                placeholder="작성자 이름을 입력하세요"
                value={author}
                onChange={(e) => setAuthor(e.target.value)}
                disabled={loading}
                maxLength={50}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="title">제목</label>
              <input
                id="title"
                type="text"
                placeholder="게시글 제목을 입력하세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                disabled={loading}
                maxLength={100}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="content">내용</label>
              <textarea
                id="content"
                placeholder="게시글 내용을 입력하세요"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                disabled={loading}
                maxLength={1000}
                rows={4}
                required
              />
            </div>

            <button type="submit" className="submit-button" disabled={loading}>
              {loading ? "등록 중..." : "📝 게시하기"}
            </button>
          </form>
        </section>

        {/* 게시글 목록 */}
        <section className="posts-section">
          <div className="posts-header">
            <h2>📋 게시글 목록</h2>
            <button
              onClick={fetchPosts}
              className="refresh-button"
              disabled={loading}
            >
              {loading ? "로딩 중..." : "🔄 새로고침"}
            </button>
          </div>

          {loading && posts.length === 0 ? (
            <div className="loading">
              <p>게시글을 불러오는 중...</p>
            </div>
          ) : posts.length === 0 ? (
            <div className="no-posts">
              <p>😔 아직 게시글이 없습니다.</p>
              <p>첫 번째 게시글을 작성해보세요!</p>
            </div>
          ) : (
            <div className="posts-list">
              {posts.map((post) => (
                <article key={post.id} className="post-card">
                  <div className="post-header">
                    <h3 className="post-title">{post.title}</h3>
                    <div className="post-meta">
                      <span className="post-author">👤 {post.author}</span>
                      <span className="post-date">
                        🕒 {formatDate(post.createdAt)}
                      </span>
                    </div>
                  </div>
                  <div className="post-content">
                    <p>{post.content}</p>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
};

export default App;
