import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

function BookView() {
  const { id } = useParams();
  const [book, setBook] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/api/v1/book/${id}`)
      .then(response => response.json())
      .then(data => {
        setBook(data);
        return fetch(`/api/v1/comment?bookId=${id}`);
      })
      .then(response => response.json())
      .then(data => {
        setComments(data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error:', error);
        setLoading(false);
      });
  }, [id]);

  const handleDeleteComment = (commentId) => {
    if (confirm('Удалить комментарий?')) {
      fetch(`/api/v1/comment/${commentId}`, { method: 'DELETE' })
        .then(() => fetch(`/api/v1/comment?bookId=${id}`))
        .then(response => response.json())
        .then(data => setComments(data))
        .catch(error => console.error('Error:', error));
    }
  };

  const handleAddComment = (e) => {
    e.preventDefault();
    const text = e.target.comment.value;
    if (!text.trim()) return;

    fetch('/api/v1/comment', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text, bookId: id })
    })
      .then(() => fetch(`/api/v1/comment?bookId=${id}`))
      .then(response => response.json())
      .then(data => {
        setComments(data);
        e.target.reset();
      })
      .catch(error => console.error('Error:', error));
  };

  if (loading) return <div>Загрузка...</div>;
  if (!book) return <div>Книга не найдена</div>;

  return (
    <div>
      <h2>{book.title}</h2>
      <p><strong>Автор:</strong> {book.author.fullName}</p>
      <p><strong>Жанры:</strong> {book.genres.map(g => g.name).join(', ')}</p>
      
      <Link to="/books">Назад к списку</Link> |{' '}
      <Link to={`/books/${id}/edit`}>Редактировать</Link>

      <hr />
      <h3>Комментарии</h3>
      
      <form onSubmit={handleAddComment}>
        <textarea name="comment" rows="3" cols="50" placeholder="Ваш комментарий..."></textarea>
        <br />
        <button type="submit">Добавить комментарий</button>
      </form>

      {comments.length === 0 && <p>Нет комментариев</p>}
      {comments.map(comment => (
        <div key={comment.id}>
          <p>{comment.text}</p>
          <button onClick={() => handleDeleteComment(comment.id)}>Удалить</button>
          <hr />
        </div>
      ))}
    </div>
  );
}

export default BookView;