import { Navigate, Route, Routes } from 'react-router-dom';
import NavBar from './components/NavBar';
import ProtectedRoute from './components/ProtectedRoute';
import { AuthProvider } from './hooks/useAuth';
import AuthorForm from './pages/AuthorForm';
import AuthorList from './pages/AuthorList';
import AuthorView from './pages/AuthorView';
import BookForm from './pages/BookForm';
import BookList from './pages/BookList';
import BookView from './pages/BookView';
import GenreForm from './pages/GenreForm';
import GenreList from './pages/GenreList';
import GenreView from './pages/GenreView';
import LoginForm from './pages/LoginForm';

function AppContent() {
  return (
    <div>
      <NavBar />
      <div style={{ padding: '20px' }}>
        <Routes>
          <Route path="/" element={<Navigate to="/books" replace />} />
          
          <Route path="/books" element={
            <ProtectedRoute>
              <BookList />
            </ProtectedRoute>
          } />
          <Route path="/books/create" element={
            <ProtectedRoute>
              <BookForm />
            </ProtectedRoute>
          } />
          <Route path="/books/:id" element={
            <ProtectedRoute>
              <BookView />
            </ProtectedRoute>
          } />
          <Route path="/books/:id/edit" element={
            <ProtectedRoute>
              <BookForm />
            </ProtectedRoute>
          } />
          
          <Route path="/authors" element={
            <ProtectedRoute>
              <AuthorList />
            </ProtectedRoute>
          } />
          <Route path="/authors/create" element={
            <ProtectedRoute>
              <AuthorForm />
            </ProtectedRoute>
          } />
          <Route path="/authors/:id" element={
            <ProtectedRoute>
              <AuthorView />
            </ProtectedRoute>
          } />
          <Route path="/authors/:id/edit" element={
            <ProtectedRoute>
              <AuthorForm />
            </ProtectedRoute>
          } />
          
          <Route path="/genres" element={
            <ProtectedRoute>
              <GenreList />
            </ProtectedRoute>
          } />
          <Route path="/genres/create" element={
            <ProtectedRoute>
              <GenreForm />
            </ProtectedRoute>
          } />
          <Route path="/genres/:id" element={
            <ProtectedRoute>
              <GenreView />
            </ProtectedRoute>
          } />
          <Route path="/genres/:id/edit" element={
            <ProtectedRoute>
              <GenreForm />
            </ProtectedRoute>
          } />

          <Route path="/login" element={<LoginForm />} />
        </Routes>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;